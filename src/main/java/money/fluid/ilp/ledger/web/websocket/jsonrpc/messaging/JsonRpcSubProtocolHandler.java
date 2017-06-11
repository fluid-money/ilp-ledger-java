/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package money.fluid.ilp.ledger.web.websocket.jsonrpc.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import money.fluid.ilp.ledger.exceptions.problems.websockets.InvalidMessagePayloadProblem;
import money.fluid.ilp.ledger.model.ids.LedgerAccountId;
import money.fluid.ilp.ledger.web.UrlService;
import money.fluid.ilp.ledger.web.websocket.model.LedgerEventType;
import money.fluid.ilp.ledger.web.websocket.model.LedgerResourceType;
import money.fluid.ilp.ledger.web.websocket.model.SubscribeToAccountRequest;
import money.fluid.ilp.ledger.web.websocket.model.SubscribeToAccountResponse;
import money.fluid.ilp.ledger.web.websocket.model.notifications.TransferCreatedNotification;
import money.fluid.ilp.ledger.web.websocket.model.notifications.TransferNotificationParams;
import org.apache.commons.lang3.StringUtils;
import org.ilpx.ledger.core.events.LedgerTransferEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpAttributes;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.DestinationUserNameProvider;
import org.springframework.messaging.support.AbstractMessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ImmutableMessageChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurationSupport;
import org.springframework.web.socket.messaging.DefaultSimpUserRegistry;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SubProtocolHandler;

import java.io.Serializable;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static money.fluid.ilp.ledger.web.websocket.jsonrpc.messaging.MessageHeaders.*;
import static org.springframework.messaging.simp.SimpMessageType.*;

/**
 * A {@link SubProtocolHandler} for JsonRpc that supports ILP Common Ledger API messages using JsonRpc 2.0.
 * <p>
 * This handler supports the following commands:
 * <pre>
 *     <ul>
 *         <li>subscribe_account</li>
 *     </ul>
 * </pre>
 * <p>
 * In order to handle subscribers, the system will create a destination with the following format:
 * <pre>
 *     /accounts/{account_id}/{event_type}
 * </pre>
 * <p>
 * Since there are multiple event types (e.g., transferFunds.create and transferFunds.update), subscribers will be added to a
 * variety of destinations, one for each event type.
 */
@Component
public class JsonRpcSubProtocolHandler implements SubProtocolHandler, ApplicationEventPublisherAware {

    private static final String TOPIC__ACCOUNTS = "/topic/accounts/";
    private static final String TRANSFER = LedgerResourceType.TRANSFER.getValue();

    // --> /topic/accounts/transferFunds
    public static final String DESTINATION__TRANSFER = TOPIC__ACCOUNTS + TRANSFER;

    /**
     * This handler supports assembling large STOMP messages split into multiple
     * WebSocket messages and STOMP clients (like stomp.js) indeed split large STOMP
     * messages at 16K boundaries. Therefore the WebSocket server input message
     * buffer size must allow 16K at least plus a little extra for SockJS framing.
     */
    public static final int MINIMUM_WEBSOCKET_MESSAGE_SIZE = 16 * 1024 + 256;

    /**
     * The name of the header set on the CONNECTED frame indicating the name
     * of the user authenticated on the WebSocket session.
     */
    // public static final String CONNECTED_USER_HEADER = "user-name";

    private static final Logger logger = LoggerFactory.getLogger(JsonRpcSubProtocolHandler.class);

    private static final byte[] EMPTY_PAYLOAD = new byte[0];

    private final ObjectMapper objectMapper;

    private JsonRpcSubProtocolErrorHandler errorHandler;

    // TODO: Can this be reduced?  Determine what the max should be.
    private int messageSizeLimit = 64 * 1024;

    // TODO Fix this for deprecation warnings...
    private org.springframework.messaging.simp.user.UserSessionRegistry userSessionRegistry;

    //private final StompEncoder stompEncoder = new StompEncoder();

    //private final StompDecoder stompDecoder = new StompDecoder();

    //private final Map<String, BufferingStompDecoder> decoders = new ConcurrentHashMap<String, BufferingStompDecoder>();

    //private MessageHeaderInitializer headerInitializer;

    // A Map of sessionIds that is keyed by subscriberId.  This allows notifications to be sent back "out" to the proper client.
    //private final Map<String, String> sessionIds = new ConcurrentHashMap<>();

    private final UrlService urlService;

    private Boolean immutableMessageInterceptorPresent;

    private ApplicationEventPublisher eventPublisher;

    // TODO: This Stats appears to be for tracking STOMP connect/disconnect sessions.  However, for ILP, the Websocket
    // session appears to be sufficient, since it is also tracking its own stats.  See SubProtocolWebSocketHandler.
    //private final Stats stats = new Stats();

    /**
     * Required-args Constructor.
     *
     * @param objectMapper
     * @param urlService
     */
    public JsonRpcSubProtocolHandler(
            final ObjectMapper objectMapper,
            final UrlService urlService
    ) {
        this.objectMapper = requireNonNull(objectMapper);
        this.urlService = requireNonNull(urlService);
    }

    /**
     * Configure a handler for error messages sent to clients which allows
     * customizing the error messages or preventing them from being sent.
     *
     * @param errorHandler the error handler
     */
    public void setErrorHandler(JsonRpcSubProtocolErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    /**
     * Return the configured error handler.
     */
    public JsonRpcSubProtocolErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    /**
     * Configure the maximum size allowed for an incoming STOMP message.
     * Since a STOMP message can be received in multiple WebSocket messages,
     * buffering may be required and therefore it is necessary to know the maximum
     * allowed message size.
     * <p>By default this property is set to 64K.
     *
     * @since 4.0.3
     */
    public void setMessageSizeLimit(int messageSizeLimit) {
        this.messageSizeLimit = messageSizeLimit;
    }

    /**
     * Get the configured message buffer size limit in bytes.
     *
     * @since 4.0.3
     */
    public int getMessageSizeLimit() {
        return this.messageSizeLimit;
    }

    /**
     * Provide a registry with which to register active user session ids.
     *
     * @see org.springframework.messaging.simp.user.UserDestinationMessageHandler
     * @deprecated as of 4.2 in favor of {@link DefaultSimpUserRegistry} which relies on the ApplicationContext events
     * published by this class and is created via {@link WebSocketMessageBrokerConfigurationSupport#createLocalUserRegistry
     * WebSocketMessageBrokerConfigurationSupport.createLocalUserRegistry}
     */
    @Deprecated
    public void setUserSessionRegistry(org.springframework.messaging.simp.user.UserSessionRegistry registry) {
        this.userSessionRegistry = registry;
    }

    /**
     * @deprecated as of 4.2
     */
    @Deprecated
    public org.springframework.messaging.simp.user.UserSessionRegistry getUserSessionRegistry() {
        return this.userSessionRegistry;
    }

    @Override
    public List<String> getSupportedProtocols() {
        return Arrays.asList("v20.ilpclapi.jsonrpc");
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }

    /**
     * Return a String describing internal state and counters.
     */
    // public String getStatsInfo() {
//        return this.stats.toString();
//    }

    /**
     * Handle incoming WebSocket messages from clients.
     */
    public void handleMessageFromClient(
            WebSocketSession session,
            WebSocketMessage<?> webSocketMessage,
            MessageChannel outputChannel
    ) {

        try {
            if (webSocketMessage instanceof TextMessage) {
                final JsonNode messagePayloadJsonNode = this.objectMapper.readValue(
                        ((TextMessage) webSocketMessage).getPayload(), JsonNode.class);

                final String optJsonRpc = valueAsString("jsonrpc", messagePayloadJsonNode).orElseThrow(
                        () -> new InvalidMessagePayloadProblem("jsonrpc"));
                final String method = valueAsString("method", messagePayloadJsonNode).orElseThrow(
                        () -> new InvalidMessagePayloadProblem("method"));

                if ("2.0".equals(optJsonRpc)) {
                    if (SubscribeToAccountRequest.SUBSCRIBE_ACCOUNT.equals(method)) {
                        final SubscribeToAccountResponse result = this.handleSubscribeToAccount(
                                session,
                                this.objectMapper.treeToValue(messagePayloadJsonNode, SubscribeToAccountRequest.class),
                                outputChannel
                        );

                        // The response message, per the Common Ledger API.
                        // TODO: Should this be async?  Or should this be sync?  Consider message to the list for this
                        // question, though maybe it doesn't matter.  However, the id in the payload seems to imply that it is async.
                        session.sendMessage(new TextMessage(this.objectMapper.writeValueAsBytes(result)));
                    }
                    // TODO: Implement Subscribe_Transfer?
                }
            } else {
                // TODO: Determine how to properly map this to a websocket error...see handleError?
                throw new IllegalStateException("Unexpected WebSocket message type: " + webSocketMessage);
            }
        } catch (Throwable ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Failed to parse " + webSocketMessage +
                                     " in session " + session.getId() + ". Sending IlpClapi ERROR to client.", ex);
            }
            handleError(session, ex, null);
            return;
        }
    }


    /**
     * Invoked when no
     * {@link #setErrorHandler(JsonRpcSubProtocolErrorHandler) errorHandler}
     * is configured to send an ERROR frame to the client.
     *
     * @deprecated as of Spring 4.2, in favor of {@link #setErrorHandler(JsonRpcSubProtocolErrorHandler) configuring} a
     * {@code StompSubProtocolErrorHandler}
     */
    @Deprecated
    protected void sendErrorMessage(WebSocketSession session, Throwable error) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
        headerAccessor.setMessage(error.getMessage());

        // TODO: Fix this!

//        byte[] bytes = this.stompEncoder.encode(headerAccessor.getMessageHeaders(), EMPTY_PAYLOAD);
//        try {
//            session.sendMessage(new TextMessage(bytes));
//        } catch (Throwable ex) {
//            // Could be part of normal workflow (e.g. browser tab closed)
//            logger.debug("Failed to send STOMP ERROR to client", ex);
//        }
    }

    private boolean detectImmutableMessageInterceptor(MessageChannel channel) {
        if (this.immutableMessageInterceptorPresent != null) {
            return this.immutableMessageInterceptorPresent;
        }

        if (channel instanceof AbstractMessageChannel) {
            for (ChannelInterceptor interceptor : ((AbstractMessageChannel) channel).getInterceptors()) {
                if (interceptor instanceof ImmutableMessageChannelInterceptor) {
                    this.immutableMessageInterceptorPresent = true;
                    return true;
                }
            }
        }
        this.immutableMessageInterceptorPresent = false;
        return false;
    }

    private void publishEvent(ApplicationEvent event) {
        try {
            this.eventPublisher.publishEvent(event);
        } catch (Throwable ex) {
            logger.error("Error publishing " + event, ex);
        }
    }

    /**
     * Handle SIMPLE messages going back out to WebSocket clients.
     * <p>
     * Per the CommonLedgerApi docs, "Every notification is sent at most once per WebSocket connection to the ledger,
     * even if a client is subscribed to multiple categories of message that should prompt the same notification. (For
     * example, if you are subscribed to the credit_account or debit_account of a transferFunds and subscribed to the
     * transferFunds itself, you still receive only one notification)."
     */
    @Override
    public void handleMessageToClient(final WebSocketSession session, final Message<?> message) {

        logger.info("SessionId: " + session.getId());

        final SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.getAccessor(
                message, SimpMessageHeaderAccessor.class);

//        final String destination = Optional.ofNullable(accessor.getDestination())
//                .filter((dest) -> StringUtils.isBlank(dest) == false)
//                .orElseThrow(() -> new RuntimeException("All Websocket messages must have a destination!"));

        // TODO: perform the valueOf in a try/catch and default to something?
        final LedgerEventType ledgerEventType = Optional.ofNullable(accessor.getNativeHeader(LEDGER_EVENT_TYPE))
                .filter(headers -> headers.size() > 0)
                .map(header -> header.get(0))
                .filter((lrt) -> StringUtils.isBlank(lrt) == false)
                .map(LedgerEventType::parseLedgerEventType)
                .orElseThrow(() -> new RuntimeException(
                        "All Websocket messages must have a " + LEDGER_RESOURCE_TYPE + " header!"));

        try {
            switch (ledgerEventType) {
                case TRANSFER_CREATE:
                case TRANSFER_UPDATE: {
                    // TODO: Extract to helper method...
                    final LedgerTransferEvent event = this.objectMapper.readValue(
                            ((Message<byte[]>) message).getPayload(), LedgerTransferEvent.class);

                    final Optional<String> transferNotificationId = Optional.of(UUID.randomUUID().toString());
                    final String ledgerTransferResourceUrl = this.urlService.buildLedgerTransferUrl(
                            event.getLedgerTransferId()).build().toString();
                    final TransferNotificationParams transferNotificationParams = new TransferNotificationParams(
                            transferNotificationId,
                            ledgerEventType.getValue(),
                            ledgerTransferResourceUrl,
                            Optional.empty() // TODO: Set fulfillment here!
                    );
                    final TransferCreatedNotification notification = new TransferCreatedNotification(
                            transferNotificationParams);
                    final byte[] bytes = this.objectMapper.writeValueAsBytes(notification);
                    session.sendMessage(new TextMessage(bytes));
                    break;
                }
                default: {
                    throw new RuntimeException(String.format("Uhandled Message type %s", ledgerEventType));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getSessionRegistryUserName(Principal principal) {
        String userName = principal.getName();
        if (principal instanceof DestinationUserNameProvider) {
            userName = ((DestinationUserNameProvider) principal).getDestinationUserName();
        }
        return userName;
    }

    @Override
    public String resolveSessionId(Message<?> message) {
        return SimpMessageHeaderAccessor.getSessionId(message.getHeaders());
    }

    @Override
    public void afterSessionStarted(final WebSocketSession session, final MessageChannel outputChannel) {
        if (session.getTextMessageSizeLimit() < MINIMUM_WEBSOCKET_MESSAGE_SIZE) {
            session.setTextMessageSizeLimit(MINIMUM_WEBSOCKET_MESSAGE_SIZE);
        }

        try {
            final Principal user = this.determineCurrentUser(session);
            final Message<byte[]> message = this.createConnectMessage(session);
            publishEvent(new SessionConnectedEvent(this, message, user));

            // CLAPI doesn't ACK on the connect, but instead on the SUBSCRIBE, so no output channel required here.
            //outputChannel.send(message);
        } finally {
            SimpAttributesContextHolder.resetAttributes();
        }
    }

    @Override
    public void afterSessionEnded(WebSocketSession session, CloseStatus closeStatus, MessageChannel outputChannel) {
        Principal principal = session.getPrincipal();
        if (principal != null && this.userSessionRegistry != null) {
            String userName = getSessionRegistryUserName(principal);
            this.userSessionRegistry.unregisterSessionId(userName, session.getId());
        }

        Message<byte[]> message = createDisconnectMessage(session);
        SimpAttributes simpAttributes = SimpAttributes.fromMessage(message);
        try {
            SimpAttributesContextHolder.setAttributes(simpAttributes);
            if (this.eventPublisher != null) {
                Principal user = session.getPrincipal();
                publishEvent(new SessionDisconnectEvent(this, message, session.getId(), closeStatus, user));
            }
            outputChannel.send(message);
        } finally {
            SimpAttributesContextHolder.resetAttributes();
            simpAttributes.sessionCompleted();
        }
    }

    private Message<byte[]> createConnectMessage(WebSocketSession session) {
        final SimpMessageHeaderAccessor accessor = this.headerAccessor(session, CONNECT);
        return MessageBuilder.createMessage(EMPTY_PAYLOAD, accessor.getMessageHeaders());
    }

    private Message<byte[]> createDisconnectMessage(WebSocketSession session) {
        final SimpMessageHeaderAccessor accessor = this.headerAccessor(session, DISCONNECT);
        return MessageBuilder.createMessage(EMPTY_PAYLOAD, accessor.getMessageHeaders());
    }

    /**
     * Creates a {@link Message} with an empty payload.
     *
     * @param session
     * @param subscriptionId This value is the same as {@code ledgerAccountId}, and is distinct from the websocket
     *                       requestId, which is merely used to differentiate the responses.
     * @return
     */
    // TODO: Once this stabilizes, this will always be a single destination, so make this _not_ a List?
    private List<Message<byte[]>> createSubscribeMessages(
            final WebSocketSession session,
            final String subscriptionId,
            final LedgerAccountId ledgerAccountId,
            final LedgerResourceType ledgerResourceType,
            final LedgerEventType ledgerEventType
    ) {

        Objects.requireNonNull(session);
        Objects.requireNonNull(subscriptionId);
        Objects.requireNonNull(ledgerAccountId);
        Objects.requireNonNull(ledgerResourceType);
        Objects.requireNonNull(ledgerEventType);

        // TODO: Is this the correct payload?
        final byte[] payload;
        try {
            payload = this.objectMapper.writeValueAsBytes(EMPTY_PAYLOAD);
        } catch (JsonProcessingException e) {
            // TODO: Handle this properly!
            throw new RuntimeException(e);
        }

        switch (ledgerResourceType) {
            case ACCOUNT: {
                return this.toMessagingDestinations(ledgerResourceType, ledgerEventType).stream()
                        .map(destination -> this.constructMessageForSubscribe(
                                session, SUBSCRIBE, payload, destination, subscriptionId, ledgerAccountId,
                                ledgerResourceType, ledgerEventType
                             )
                        )
                        .collect(Collectors.toList());
            }
            default: {
                logger.error("No handlers for specified LedgerResource type: {}", ledgerResourceType);
                return ImmutableList.of();
            }
        }
    }

    /**
     * In order for the eventing system to work properly, a subscription identifier needs to contain the actual resource
     * identifier, the resource type, and the event type.  This is because
     *
     * @param ledgerAccountId
     * @return
     */
    private String constructSubscriptionId(LedgerAccountId ledgerAccountId) {
        return ledgerAccountId.value();
    }

    /**
     * Per the Common Ledger API, a single Websocket request of type 'subscribe_account' "...replaces any existing
     * account subscriptions on this WebSocket connection."  Therefore, the actual internal identifier of a Subscription
     * for this type of subscription is the ledger account identifier.
     * <p>
     * NOTE: The resource type is not included in this identifier because there is a different destination topic for
     * each type of
     *
     * @param ledgerAccountId
     * @return
     */
    private String constructAccountSubscriptionId(final LedgerAccountId ledgerAccountId) {
        Objects.requireNonNull(ledgerAccountId);
        return ledgerAccountId.value();
    }


    /**
     * Per the Common Ledger API, a single Websocket session may only subscribe once to a given resource like an Account
     * or transferFunds, so a subscription Id is essentially the identifier of the resource.
     *
     * @param ledgerResourceType
     * @return
     */
    private String toSubscriptionId(LedgerResourceType ledgerResourceType, LedgerAccountId le) {
        Objects.requireNonNull(ledgerResourceType);
        return ledgerResourceType.getValue();
    }


    @Override
    public String toString() {
        return getSupportedProtocols().toString();
    }

    /**
     * Handle a "subscribe_account" request by adding the current Websocket session to a collection of subscribers for
     * the indicated event type(s).
     *
     * @param session
     * @param request
     * @return
     */
    public SubscribeToAccountResponse handleSubscribeToAccount(
            final WebSocketSession session,
            final SubscribeToAccountRequest request,
            final MessageChannel outputChannel
    ) throws JsonProcessingException {

        Objects.requireNonNull(session);
        Objects.requireNonNull(request);
        Objects.requireNonNull(outputChannel);

        // NOTE: The 'id' field from the request is merely meant as a request identifier, and not as a subscription
        // identifier supplied by the client.  The request identifier is echo'd in the response so that the Websocket
        // client can distinguish between responses.  The subscriptionId for account subscriptions is actually the
        // ledgerAccountId (e.g., 'alice') since follow-up subscription requests for the same resrouce should replace
        // any existing requests.

        final LedgerEventType ledgerEventType = request.getParams().getLedgerResourceEvent();

        final int[] numSubscribedAccounts = {0};

        // For each accountId...
        request.getParams().getAccountIds().stream()
                .map(accountId -> LedgerAccountId.of(accountId))
                .forEach(ledgerAccountId -> {

// TODO: Does the authenticated user have permission to subscribe to the indicated account?

                    // ...construct 1 or more subscribe messages that will subscribe this listener to a given topic for each
                    // event type.
                    this.createSubscribeMessages(
                            session,
                            constructAccountSubscriptionId(ledgerAccountId), // internal subscriptionId.
                            ledgerAccountId,
                            LedgerResourceType.ACCOUNT,
                            ledgerEventType
                    ).forEach(message -> {
                        try {
                            // Create a subscription message and send to the output channel so that the Websocket session will
                            // be subscribed to the proper events.
                            SimpAttributesContextHolder.setAttributesFromMessage(message);
                            final boolean sent = outputChannel.send(message);
                            numSubscribedAccounts[0]++;
                            if (sent && this.eventPublisher != null) {
                                // Send a SessionSubscribeEvent to any application listeners that might perform follow-on logic.
                                // E.g., User subscriptions are processed here.
                                // TODO: re-use the message object from above, if possible -- don't reconstruct the message again?
                                final SessionSubscribeEvent event = constructSessionSubscribeEvent(
                                        session,
                                        message,
                                        ledgerAccountId
                                );
                                publishEvent(event);
                            }
                        } finally {
                            // NOTE: This is thread-bound...
                            SimpAttributesContextHolder.resetAttributes();
                        }
                    });
                });

        return new SubscribeToAccountResponse(
                // Echo the id from the request to help distinguish responses from other messages.
                request.getId(),
                numSubscribedAccounts[0]
        );
    }

    ////////////////////////////
    // Private Helper Methods...
    ////////////////////////////

    /**
     * Construct an instance of {@link SessionSubscribeEvent} based upon the supplied message.
     */
    @VisibleForTesting
    protected SessionSubscribeEvent constructSessionSubscribeEvent(
            final WebSocketSession session,
            final Message<?> message,
            final LedgerAccountId ledgerAccountId
    ) {
        Objects.requireNonNull(session);
        Objects.requireNonNull(message);
        Objects.requireNonNull(ledgerAccountId);

        final Principal signedInUser = determineCurrentUser(session);

        final SessionSubscribeEventPayload sessionSubscribeEvent = new SessionSubscribeEventPayload(
                ledgerAccountId
        );
        final byte[] payload;
        try {
            payload = this.objectMapper.writeValueAsBytes(sessionSubscribeEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // TODO: Consider re-using the exact same message for both event, and removing thie method entirely.
        // Change the payload for this notification?
        final Message<byte[]> updatedMessage = MessageBuilder.createMessage(payload, message.getHeaders());
        return new SessionSubscribeEvent(this, updatedMessage, signedInUser);
    }

    /**
     * Helper method to construct an instance of {@link Message}.
     *
     * @param session
     * @param messageType
     * @param payload
     * @param destination
     * @param subscriptionId
     * @param ledgerAccountId
     * @return
     */
    private Message<byte[]> constructMessageForSubscribe(
            final WebSocketSession session,
            final SimpMessageType messageType,
            final byte[] payload,
            final String destination,
            final String subscriptionId,
            final LedgerAccountId ledgerAccountId,
            final LedgerResourceType ledgerResourceType,
            final LedgerEventType ledgerEventType
    ) {
        final SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(messageType);
        headerAccessor.setSessionId(session.getId());
        headerAccessor.setSessionAttributes(session.getAttributes());
        headerAccessor.setUser(session.getPrincipal());

        // Used by the other end of the messaging system to know how to route message that is merely coming from a destination...
        headerAccessor.setHeader(MessageHeaders.LEDGER_RESOURCE_TYPE, ledgerResourceType);
        headerAccessor.setHeader(MessageHeaders.LEDGER_EVENT_TYPE, ledgerEventType);

        headerAccessor.setNativeHeader("selector", this.constructSelectorHeader(ledgerEventType, ledgerAccountId));

        headerAccessor.setSubscriptionId(subscriptionId);
        headerAccessor.setDestination(destination);

        // The sessionSubscribeEventFunction of a SessionSubscribeEvent is a SessionSubscribeEventPayload.
        return MessageBuilder.createMessage(payload, headerAccessor.getMessageHeaders());
    }


    private String constructSelectorHeader(
            final LedgerEventType ledgerEventType, final LedgerAccountId ledgerAccountId
    ) {
        switch (ledgerEventType) {
            case TRANSFER_CREATE:
            case TRANSFER_UPDATE:

            {
                return "headers." + MessageHeaders.LEDGER_EVENT_TYPE + " == '" + ledgerEventType.getValue() + "' " +
                        "AND " +
                        "headers." + MessageHeaders.LEDGER_ACCOUNT_ID + " == '" + ledgerAccountId.value() + "'";
            }
            case TRANSFER_ALL:
            case ALL:
            default: {
                return "(" +
                        "headers." + MessageHeaders.LEDGER_EVENT_TYPE + " == '" + LedgerEventType.TRANSFER_CREATE.getValue() + "'" +
                        " OR " +
                        "headers." + MessageHeaders.LEDGER_EVENT_TYPE + " == '" + LedgerEventType.TRANSFER_UPDATE.getValue() + "'" +
                        ")" +
                        " AND " +
                        "headers." + MessageHeaders.LEDGER_ACCOUNT_ID + " == '" + ledgerAccountId.value() + "'";
            }
        }
    }

    /**
     * Helper method to determine the destination (for messaging purposes) that the current Websocket session should be
     * subscribed to, based upon the information found in the supplied {@code ledgerResourceEvent}.
     *
     * @param ledgerResourceType An instance of {@link LedgerResourceType}.
     * @return An optionally-present {@link String} representing the destination (for messaging purposes) to subscribe
     * the current Websocket to.
     */
    private List<String> toMessagingDestinations(
            final LedgerResourceType ledgerResourceType, final LedgerEventType ledgerEventType
    ) {
        Objects.requireNonNull(ledgerResourceType);

        final Builder<String> builder = ImmutableList.builder();

        switch (ledgerResourceType) {
            case ACCOUNT: {
                switch (ledgerEventType) {
                    // TODO: Do we even need to care about the ledgerEventType?  If something else is specified, perhaps
                    // we still add it to the transferFunds destination?
                    case TRANSFER_CREATE:
                    case TRANSFER_UPDATE:
                    case TRANSFER_ALL:
                    case ALL:
                        builder.add(DESTINATION__TRANSFER);
                        break;
                }
                break;
            }
            case TRANSFER: {
                break;
            }
            default: {
                // Do nothing!
            }
        }

        return builder.build();
    }

    private Optional<String> valueAsString(final String nodeName, final JsonNode jsonNode) {
        requireNonNull(nodeName);
        requireNonNull(jsonNode);

        if (jsonNode.has(nodeName)) {
            return Optional.ofNullable(jsonNode.get(nodeName).textValue());
        } else {
            return Optional.empty();
        }
    }

    // TODO: Implement this for real...
    @SuppressWarnings("deprecation")
    private void handleError(WebSocketSession session, Throwable ex, Message<byte[]> clientMessage) {
        if (getErrorHandler() == null) {
            sendErrorMessage(session, ex);
            return;
        }

        Message<byte[]> message = getErrorHandler().handleClientMessageProcessingError(clientMessage, ex);
        if (message == null) {
            return;
        }

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        Assert.notNull(accessor, "Expected STOMP headers");
        // TODO: Do this...
        //sendToClient(session, accessor, message.getPayload());
    }

    @RequiredArgsConstructor
    @Getter
    @ToString
    @EqualsAndHashCode
    public static final class SessionSubscribeEventPayload implements Serializable {
        //private final String sessionId;
        private final LedgerAccountId ledgerAccountId;
    }

    /**
     * Initialize an instance of {@link SimpMessageHeaderAccessor} that can then be used to assemble an instance of
     * {@link Message}.
     *
     * @param session
     * @param messageType
     * @return
     */
    private SimpMessageHeaderAccessor headerAccessor(
            final WebSocketSession session, final SimpMessageType messageType
    ) {
        final SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(messageType);
        headerAccessor.setSessionId(session.getId());
        headerAccessor.setSessionAttributes(session.getAttributes());
        headerAccessor.setUser(session.getPrincipal());
        return headerAccessor;
    }

    /**
     * // TODO: Determine which events the anonymous user may be subscribed to?  Consider requiring all subscribers to
     * be authenticated...
     */
    private Principal determineCurrentUser(final WebSocketSession session) {
        // If no user is signed-in, then return null.
        if (session.getPrincipal() == null) {
            //return () -> session.value();
            return null;
        } else {
            return session.getPrincipal();
        }
    }
}
