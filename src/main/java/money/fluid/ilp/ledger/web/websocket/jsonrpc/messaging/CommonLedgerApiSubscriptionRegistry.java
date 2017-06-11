package money.fluid.ilp.ledger.web.websocket.jsonrpc.messaging;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.broker.AbstractSubscriptionRegistry;
import org.springframework.messaging.simp.broker.DefaultSubscriptionRegistry;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.PathMatcher;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;

/**
 * An implementation of {@link AbstractSubscriptionRegistry} that allows the Subscription to be replaced on a per
 * account basis.
 */
@ToString
@EqualsAndHashCode
public class CommonLedgerApiSubscriptionRegistry extends AbstractSubscriptionRegistry {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    // This is the ultimate source of truth for all subscriptions...there is a secondary cache that allows for lookups by destination from this object.
    private final SessionSubscriptionRegistry subscriptionRegistry = new SessionSubscriptionRegistry();

    // Cache for quick lookup of a unique Set of subscriptions by session id.  The subscriptionId must not be duplicated, so this cache
    // stores a Key(sessionId) with a Map value of a Set<subscription ids>.  This will later be transformed to the
    // MultiValueMap<String,String> subscriptions that is required by the API.
    // SessionId --> Set<SubscriptionId>
    private LoadingCache<String, Set<String>> subscriptionIdsPerSessionCache = CacheBuilder.newBuilder()
            // 1000 sessions max in the cache...
            .maximumSize(1000)
            // Set to 0 to simulate disabled caching...
            //.expireAfterWrite(10, TimeUnit.SECONDS)
            .removalListener(
                    (notification) -> logger.info("Subscription {}:{} evicted because: {}", notification.getKey(),
                                                  notification.getValue(), notification.getCause()
                    ))
            .build(new CacheLoader<String, Set<String>>() {
                public Set<String> load(String sessionId) { // no checked exception
                    final SessionSubscriptionInfo sessionSubscriptionInfo = subscriptionRegistry.getSubscriptions(
                            sessionId);

                    // For each destination, grab all subscriptions...
                    final Set<String> subscriptionIdSet = Sets.newConcurrentHashSet();

                    sessionSubscriptionInfo.getDestinations().stream()
                            .forEach(destination -> {
                                sessionSubscriptionInfo.getSubscriptions(destination)
                                        .stream()
                                        .map(sub -> sub.getId())
                                        .forEach(subscriptionIdSet::add);
                            });

                    return subscriptionIdSet;
                }
            });

    /**
     * Default maximum number of entries for the destination cache: 1024
     */
    public static final int DEFAULT_CACHE_LIMIT = 1024;

    private PathMatcher pathMatcher = new AntPathMatcher();

    private volatile int cacheLimit = DEFAULT_CACHE_LIMIT;

    private volatile boolean selectorHeaderInUse = false;

    private String selectorHeaderName = "selector";

    private final ExpressionParser expressionParser = new SpelExpressionParser();

    /**
     * Specify the {@link PathMatcher} to use.
     */
    public void setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    /**
     * Return the configured {@link PathMatcher}.
     */
    public PathMatcher getPathMatcher() {
        return this.pathMatcher;
    }

    /**
     * Specify the maximum number of entries for the resolved destination cache.
     * Default is 1024.
     */
    public void setCacheLimit(int cacheLimit) {
        this.cacheLimit = cacheLimit;
    }

    /**
     * Return the maximum number of entries for the resolved destination cache.
     */
    public int getCacheLimit() {
        return this.cacheLimit;
    }

    /**
     * Return the name for the selector header.
     *
     * @since 4.2
     */
    public String getSelectorHeaderName() {
        return this.selectorHeaderName;
    }

    @Override
    protected void addSubscriptionInternal(String sessionId, String subsId, String destination, Message<?> message) {
        Expression expression = null;
        org.springframework.messaging.MessageHeaders headers = message.getHeaders();
        String selector = SimpMessageHeaderAccessor.getFirstNativeHeader(getSelectorHeaderName(), headers);
        if (selector != null) {
            try {
                expression = this.expressionParser.parseExpression(selector);
                this.selectorHeaderInUse = true;
                if (logger.isTraceEnabled()) {
                    logger.trace("Subscription selector: [" + selector + "]");
                }
            } catch (Throwable ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to parse selector: " + selector, ex);
                }
            }
        }
        this.subscriptionRegistry.addSubscription(sessionId, subsId, destination, expression);

        // Need to add the current subscriptionId to the Set<SubscriptionId> for the indicated sessionId, if present.
        try {
            // Will trigger a reload of sessions if the sessionId is not found...
            this.subscriptionIdsPerSessionCache.get(sessionId).add(subsId);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void removeSubscriptionInternal(String sessionId, String subsId, Message<?> message) {
        final SessionSubscriptionInfo info = this.subscriptionRegistry.getSubscriptions(sessionId);
        if (info != null) {
            final String destination = info.removeSubscription(subsId);
            if (destination != null) {
                // Need to remove the current subscriptionId from the Set<SubscriptionId> in the cache.
                Optional.ofNullable(this.subscriptionIdsPerSessionCache.getIfPresent(sessionId))
                        .ifPresent(sessionSubscriptions -> sessionSubscriptions.remove(subsId));
            }
        }
    }

    @Override
    public void unregisterAllSubscriptions(final String sessionId) {
        final SessionSubscriptionInfo info = this.subscriptionRegistry.removeSubscriptions(sessionId);
        if (info != null) {
            // Remove all subscriptions for the indicated sessionId.
            this.subscriptionIdsPerSessionCache.invalidate(sessionId);
        }
    }

    /**
     * For a given destination (e.g., "/topic/transfers"), return all subscription ids that are subscribed to the
     * destination.
     *
     * @param destination
     * @param message
     * @return
     */
    @Override
    protected MultiValueMap<String, String> findSubscriptionsInternal(String destination, Message<?> message) {

        // In order for this method to complete, it must query the subscriptionRegistry to find all sessions that might
        // have a subscription to the indicated "destination".  This is a potentially expensive query, so a LoadingCache
        // is used to perform the expensive computation, then cache the results.  Even though this method cycles through
        // all subscriptions (i.e., sessionIds) in the registry, the actual expensive portion of the calculation is the
        // stuff performed in the Loading method, so this method should be O(N) expensive, with N=NumSessions connected
        // to this machine.  For improved performance, we might create a better caching system.

        final MultiValueMap<String, String> result = new LinkedMultiValueMap<>();

        // Get all subscriptions for all Websocket sessions...
        this.subscriptionRegistry.getAllSubscriptions().stream()
                .forEach(sessionSubscriptionInfo -> {
                    final String sessionId = sessionSubscriptionInfo.getSessionId();
                    try {
                        subscriptionIdsPerSessionCache.get(sessionId).stream()
                                .forEach(subscriptionId -> result.add(sessionId, subscriptionId));
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                });

        return filterSubscriptions(result, message);
    }

    private MultiValueMap<String, String> filterSubscriptions(
            MultiValueMap<String, String> allMatches, Message<?> message
    ) {

        if (!this.selectorHeaderInUse) {
            return allMatches;
        }
        EvaluationContext context = null;
        MultiValueMap<String, String> result = new LinkedMultiValueMap<String, String>(allMatches.size());
        for (String sessionId : allMatches.keySet()) {
            for (String subId : allMatches.get(sessionId)) {
                SessionSubscriptionInfo info = this.subscriptionRegistry.getSubscriptions(sessionId);
                if (info == null) {
                    continue;
                }
                Subscription sub = info.getSubscription(subId);
                if (sub == null) {
                    continue;
                }
                Expression expression = sub.getSelectorExpression();
                if (expression == null) {
                    result.add(sessionId, subId);
                    continue;
                }
                if (context == null) {
                    context = new StandardEvaluationContext(message);
                    context.getPropertyAccessors().add(new SimpMessageHeaderPropertyAccessor());
                }
                try {
                    if (expression.getValue(context, boolean.class)) {
                        result.add(sessionId, subId);
                    }
                } catch (SpelEvaluationException ex) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Failed to evaluate selector: " + ex.getMessage());
                    }
                } catch (Throwable ex) {
                    logger.debug("Failed to evaluate selector", ex);
                }
            }
        }
        return result;
    }

    /**
     * Provide access to session subscriptions by sessionId.
     */
    private static class SessionSubscriptionRegistry {

        // sessionId -> SessionSubscriptionInfo
        private final ConcurrentMap<String, SessionSubscriptionInfo> sessions = new ConcurrentHashMap<>();

        public SessionSubscriptionInfo getSubscriptions(String sessionId) {
            return this.sessions.get(sessionId);
        }

        public Collection<SessionSubscriptionInfo> getAllSubscriptions() {
            return this.sessions.values();
        }

        public SessionSubscriptionInfo addSubscription(
                String sessionId, String subscriptionId,
                String destination, Expression selectorExpression
        ) {

            SessionSubscriptionInfo info = this.sessions.get(sessionId);
            if (info == null) {
                info = new SessionSubscriptionInfo(sessionId);
                SessionSubscriptionInfo value = this.sessions.putIfAbsent(sessionId, info);
                if (value != null) {
                    info = value;
                }
            }
            info.addSubscription(destination, subscriptionId, selectorExpression);
            return info;
        }

        public SessionSubscriptionInfo removeSubscriptions(String sessionId) {
            return this.sessions.remove(sessionId);
        }

        @Override
        public String toString() {
            return "registry[" + this.sessions.size() + " sessions]";
        }
    }


    /**
     * Hold a subscription to a specified destination, for an individual Websocket session.  Unlike the implementation
     * found in {@link DefaultSubscriptionRegistry}, this implementation only allows a single subscription per
     * destination.  In this way, multiple subscription requests using the same or different subscription identifiers
     * will always replace an existing subscription to a destination, if it exists.
     */
    private static class SessionSubscriptionInfo {

        private final String sessionId;

        // Per https://jira.spring.io/browse/SPR-15229, the intent of the subscriptionId is to allow the same connection
        // to have multiple subscriptions to the same destination.  However, in CLAPI, a single connection should only
        // ever have a single subscription to a given account/transferFunds resource.  Thus, we can use a fixed subscriptionId
        // for all subscription requests.

        // destination -> Map<AccountId, Subscription>
        private final Map<String, Set<Subscription>> destinationLookup = new ConcurrentHashMap<>(4);

        public SessionSubscriptionInfo(String sessionId) {
            Assert.notNull(sessionId, "sessionId must not be null");
            this.sessionId = sessionId;
        }

        public String getSessionId() {
            return this.sessionId;
        }

        public Set<String> getDestinations() {
            return this.destinationLookup.keySet();
        }

        public Set<Subscription> getSubscriptions(String destination) {
            return this.destinationLookup.get(destination);
        }

        public Subscription getSubscription(String subscriptionId) {
            for (String destination : this.destinationLookup.keySet()) {
                Set<Subscription> subs = this.destinationLookup.get(destination);
                if (subs != null) {
                    for (Subscription sub : subs) {
                        if (sub.getId().equalsIgnoreCase(subscriptionId)) {
                            return sub;
                        }
                    }
                }
            }
            return null;
        }

        public void addSubscription(String destination, String subscriptionId, Expression selectorExpression) {
            Set<Subscription> subs = this.destinationLookup.get(destination);
            if (subs == null) {
                synchronized (this.destinationLookup) {
                    subs = this.destinationLookup.get(destination);
                    if (subs == null) {
                        subs = new CopyOnWriteArraySet<>();
                        this.destinationLookup.put(destination, subs);
                    }
                }
            }
            final Subscription sub = new Subscription(subscriptionId, selectorExpression);

            // Set.add only adds if an item is not present, per equals.  Thus, to replace by id with a different  selector,
            // we need to remove and then add.  Conversely, adding the selector to the .equals method of Subscription
            // would allow two subscriptions to a single account.
            subs.remove(sub);
            subs.add(sub);
        }


        public String removeSubscription(String subscriptionId) {
            for (String destination : this.destinationLookup.keySet()) {
                Set<Subscription> subs = this.destinationLookup.get(destination);
                if (subs != null) {
                    for (Subscription sub : subs) {
                        if (sub.getId().equals(subscriptionId) && subs.remove(sub)) {
                            synchronized (this.destinationLookup) {
                                if (subs.isEmpty()) {
                                    this.destinationLookup.remove(destination);
                                }
                            }
                            return destination;
                        }
                    }
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return "[sessionId=" + this.sessionId + ", subscriptions=" + this.destinationLookup + "]";
        }
    }

    @RequiredArgsConstructor
    @Getter
    @EqualsAndHashCode(of = {"id"}) // Necessary for the Set to work properly...
    private static class Subscription {

        private final String id;

        private final Expression selectorExpression;

        @Override
        public String toString() {
            return "subscription(id=" + this.id + ")";
        }
    }

    private static class SimpMessageHeaderPropertyAccessor implements PropertyAccessor {

        @Override
        public Class<?>[] getSpecificTargetClasses() {
            return new Class<?>[]{org.springframework.messaging.MessageHeaders.class};
        }

        @Override
        public boolean canRead(EvaluationContext context, Object target, String name) {
            return true;
        }

        @Override
        public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
            org.springframework.messaging.MessageHeaders headers = (MessageHeaders) target;
            SimpMessageHeaderAccessor accessor =
                    MessageHeaderAccessor.getAccessor(headers, SimpMessageHeaderAccessor.class);
            Object value;
            if ("destination".equalsIgnoreCase(name)) {
                value = accessor.getDestination();
            } else {
                value = accessor.getFirstNativeHeader(name);
                if (value == null) {
                    value = headers.get(name);
                }
            }
            return new TypedValue(value);
        }

        @Override
        public boolean canWrite(EvaluationContext context, Object target, String name) {
            return false;
        }

        @Override
        public void write(EvaluationContext context, Object target, String name, Object value) {
        }
    }
}
