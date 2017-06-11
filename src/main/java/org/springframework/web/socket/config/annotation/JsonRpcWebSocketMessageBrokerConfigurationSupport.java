package org.springframework.web.socket.config.annotation;

import com.fasterxml.jackson.databind.ObjectMapper;
import money.fluid.ilp.ledger.datastore.jpa.subscriptions.SubscriptionRepository;
import money.fluid.ilp.ledger.web.UrlService;
import money.fluid.ilp.ledger.web.websocket.jsonrpc.messaging.CommonLedgerApiSubscriptionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.simp.SimpSessionScope;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;
import org.springframework.messaging.simp.broker.AbstractBrokerMessageHandler;
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler;
import org.springframework.messaging.simp.broker.SubscriptionRegistry;
import org.springframework.messaging.simp.config.AbstractMessageBrokerConfiguration;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.simp.user.UserSessionRegistryAdapter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;
import org.springframework.web.socket.messaging.DefaultSimpUserRegistry;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;
import org.springframework.web.socket.messaging.WebSocketAnnotationMethodMessageHandler;

import java.util.Objects;

/**
 * Extends {@link AbstractMessageBrokerConfiguration} and adds configuration for
 * receiving and responding to JsonRPC 2.0 messages from WebSocket clients.
 * <p>
 * <p>Typically used in conjunction with {@link EnableJsonRpcWebSocketMessageBroker} but can also be extended directly.
 */
public abstract class JsonRpcWebSocketMessageBrokerConfigurationSupport extends AbstractMessageBrokerConfiguration {

    private WebSocketTransportRegistration transportRegistration;

    @Override
    protected SimpAnnotationMethodMessageHandler createAnnotationMethodMessageHandler() {
        return new WebSocketAnnotationMethodMessageHandler(
                clientInboundChannel(), clientOutboundChannel(), brokerMessagingTemplate()
        );
    }

    @Override
    protected SimpUserRegistry createLocalUserRegistry() {
        org.springframework.messaging.simp.user.UserSessionRegistry sessionRegistry = userSessionRegistry();
        if (sessionRegistry != null) {
            return new UserSessionRegistryAdapter(sessionRegistry);
        }
        return new DefaultSimpUserRegistry();
    }

    // Part of the parent interface, but not used by JsonRpc, so can be ignored...
    @Bean
    public HandlerMapping jsonRpcWebSocketHandlerMapping() {
        WebSocketHandler handler = decorateWebSocketHandler(subProtocolWebSocketHandler());
        WebMvcJsonRpcEndpointRegistry registry = new WebMvcJsonRpcEndpointRegistry(
                handler,
                getTransportRegistration(),
                // TODO: Use SimpleUserRegistry...
                userSessionRegistry(),
                objectMapper,
                subscriptionRepository,
                this.urlService
        );
//
        registry.setApplicationContext(getApplicationContext());
        registerJsonRpcEndpoints(registry);
        return registry.getHandlerMapping();
    }

    @Autowired
    UrlService urlService;

    @Bean
    public WebSocketHandler subProtocolWebSocketHandler() {
        return new SubProtocolWebSocketHandler(clientInboundChannel(), clientOutboundChannel());
    }

    protected WebSocketHandler decorateWebSocketHandler(WebSocketHandler handler) {
        for (WebSocketHandlerDecoratorFactory factory : getTransportRegistration().getDecoratorFactories()) {
            handler = factory.decorate(handler);
        }
        return handler;
    }

    protected final WebSocketTransportRegistration getTransportRegistration() {
        if (this.transportRegistration == null) {
            this.transportRegistration = new WebSocketTransportRegistration();
            configureWebSocketTransport(this.transportRegistration);
        }
        return this.transportRegistration;
    }

    protected void configureWebSocketTransport(WebSocketTransportRegistration registry) {
    }

    protected abstract void registerJsonRpcEndpoints(JsonRpcEndpointRegistry registry);

    @Bean
    public static CustomScopeConfigurer webSocketScopeConfigurer() {
        CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        configurer.addScope("websocket", new SimpSessionScope());
        return configurer;
    }

    // TODO: Consider extending SimpleBrokerMessageHandler for JsonRPC?
    @Bean
    public WebSocketMessageBrokerStats webSocketMessageBrokerStats() {
        AbstractBrokerMessageHandler relayBean = simpleBrokerMessageHandler();
        SimpleBrokerMessageHandler brokerRelay = (relayBean instanceof SimpleBrokerMessageHandler ?
                (SimpleBrokerMessageHandler) relayBean : null);

        // Ensure JsonRpc endpoints are registered
        jsonRpcWebSocketHandlerMapping();

        WebSocketMessageBrokerStats stats = new WebSocketMessageBrokerStats();
        stats.setSubProtocolWebSocketHandler((SubProtocolWebSocketHandler) subProtocolWebSocketHandler());
        //stats.setStompBrokerRelay(brokerRelay);
        //stats.set
        stats.setInboundChannelExecutor(clientInboundChannelExecutor());
        stats.setOutboundChannelExecutor(clientOutboundChannelExecutor());
        //stats.setSockJsTaskScheduler(messageBrokerTaskScheduler());
        return stats;
    }

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    SubscriptionRepository subscriptionRepository;

    // TODO: Inject regular ObjectMapper here?
//    @Override
//    protected MappingJackson2MessageConverter createJacksonConverter() {
//        MappingJackson2MessageConverter messageConverter = super.createJacksonConverter();
//        // Use Jackson builder in order to have JSR-310 and Joda-Time modules registered automatically
//        messageConverter.setObjectMapper(Jackson2ObjectMapperBuilder.json()
//                                                 .applicationContext(this.getApplicationContext()).build());
//        return messageConverter;
//    }

    /**
     * Overidden here in order to call {@link SimpleBrokerMessageHandler#setSubscriptionRegistry(SubscriptionRegistry)}
     * with a new {@link SubscriptionRegistry} that allows us to customize for the Common Ledger API requirement that
     * says a single Websocket connection should have only one subscription per account.
     *
     * @return
     */
    @Override
    @Bean
    public AbstractBrokerMessageHandler simpleBrokerMessageHandler() {

        final AbstractBrokerMessageHandler handler = super.simpleBrokerMessageHandler();

        if (SimpleBrokerMessageHandler.class.isAssignableFrom(handler.getClass())) {
            ((SimpleBrokerMessageHandler) handler).setSubscriptionRegistry(new CommonLedgerApiSubscriptionRegistry());
        }

        return Objects.requireNonNull(handler, "No Simple Broker Registered!");
    }

}
