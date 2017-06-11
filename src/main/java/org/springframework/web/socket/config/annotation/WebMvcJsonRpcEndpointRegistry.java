package org.springframework.web.socket.config.annotation;

import com.fasterxml.jackson.databind.ObjectMapper;
import money.fluid.ilp.ledger.datastore.jpa.subscriptions.SubscriptionRepository;
import money.fluid.ilp.ledger.web.UrlService;
import money.fluid.ilp.ledger.web.websocket.jsonrpc.messaging.JsonRpcSubProtocolErrorHandler;
import money.fluid.ilp.ledger.web.websocket.jsonrpc.messaging.JsonRpcSubProtocolHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.simp.user.UserSessionRegistry;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;
import org.springframework.web.socket.server.support.WebSocketHandlerMapping;
import org.springframework.web.util.UrlPathHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * A registry for JsonRpc over WebSocket endpoints that maps the endpoints with a
 * {@link org.springframework.web.servlet.HandlerMapping} for use in Spring MVC.
 */
public class WebMvcJsonRpcEndpointRegistry implements JsonRpcEndpointRegistry {

    private final WebSocketHandler webSocketHandler;

    private final ObjectMapper objectMapper;

    private final SubscriptionRepository subscriptionRepository;
    private final SubProtocolWebSocketHandler subProtocolWebSocketHandler;
    private final JsonRpcSubProtocolHandler jsonRpcHandler;
    private final List<WebMvcJsonRpcWebSocketEndpointRegistration> registrations =
            new ArrayList<>();
    private int order = 1;
    private UrlPathHelper urlPathHelper;
    private final UrlService urlService;

    public WebMvcJsonRpcEndpointRegistry(
            WebSocketHandler webSocketHandler,
            WebSocketTransportRegistration transportRegistration,
            // TODO: Stop using this and use SimpleUserRegistry instead...
            UserSessionRegistry userSessionRegistry,
            final ObjectMapper objectMapper,
            SubscriptionRepository subscriptionRepository,
            final UrlService urlService
    ) {
        Assert.notNull(transportRegistration, "WebSocketTransportRegistration is required");

        this.objectMapper = requireNonNull(objectMapper);
        this.subscriptionRepository = requireNonNull(subscriptionRepository);
        this.urlService = Objects.requireNonNull(urlService);

        this.webSocketHandler = requireNonNull(webSocketHandler, "WebSocketHandler is required");
        this.subProtocolWebSocketHandler = unwrapSubProtocolWebSocketHandler(webSocketHandler);

        if (transportRegistration.getSendTimeLimit() != null) {
            this.subProtocolWebSocketHandler.setSendTimeLimit(transportRegistration.getSendTimeLimit());
        }
        if (transportRegistration.getSendBufferSizeLimit() != null) {
            this.subProtocolWebSocketHandler.setSendBufferSizeLimit(transportRegistration.getSendBufferSizeLimit());
        }

        this.jsonRpcHandler = new JsonRpcSubProtocolHandler(this.objectMapper, this.urlService);
        // TODO: Fix this!
        this.jsonRpcHandler.setUserSessionRegistry(userSessionRegistry);

        if (transportRegistration.getMessageSizeLimit() != null) {
            this.jsonRpcHandler.setMessageSizeLimit(transportRegistration.getMessageSizeLimit());
        }
    }

    private static SubProtocolWebSocketHandler unwrapSubProtocolWebSocketHandler(WebSocketHandler handler) {
        WebSocketHandler actual = WebSocketHandlerDecorator.unwrap(handler);
        if (!(actual instanceof SubProtocolWebSocketHandler)) {
            throw new IllegalArgumentException("No SubProtocolWebSocketHandler in " + handler);
        }
        return (SubProtocolWebSocketHandler) actual;
    }


    @Override
    public JsonRpcWebSocketEndpointRegistration addEndpoint(String... paths) {
        this.subProtocolWebSocketHandler.addProtocolHandler(this.jsonRpcHandler);
        WebMvcJsonRpcWebSocketEndpointRegistration registration =
                new WebMvcJsonRpcWebSocketEndpointRegistration(paths, this.webSocketHandler);
        this.registrations.add(registration);
        return registration;
    }

    protected int getOrder() {
        return this.order;
    }

    /**
     * Set the order for the resulting
     * {@link org.springframework.web.servlet.HandlerMapping}
     * relative to other handler mappings configured in Spring MVC.
     * <p>The default value is 1.
     */
    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    protected UrlPathHelper getUrlPathHelper() {
        return this.urlPathHelper;
    }

    /**
     * Set the UrlPathHelper to configure on the {@code HandlerMapping}
     * used to map handshake requests.
     */
    @Override
    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
        this.urlPathHelper = urlPathHelper;
    }

    @Override
    public WebMvcJsonRpcEndpointRegistry setErrorHandler(JsonRpcSubProtocolErrorHandler errorHandler) {
        this.jsonRpcHandler.setErrorHandler(errorHandler);
        return this;
    }

    protected void setApplicationContext(ApplicationContext applicationContext) {
        this.jsonRpcHandler.setApplicationEventPublisher(applicationContext);
    }


    /**
     * Return a handler mapping with the mapped ViewControllers; or {@code null}
     * in case of no registrations.
     */
    public AbstractHandlerMapping getHandlerMapping() {
        Map<String, Object> urlMap = new LinkedHashMap<String, Object>();
        for (WebMvcJsonRpcWebSocketEndpointRegistration registration : this.registrations) {
            MultiValueMap<HttpRequestHandler, String> mappings = registration.getMappings();
            for (HttpRequestHandler httpHandler : mappings.keySet()) {
                for (String pattern : mappings.get(httpHandler)) {
                    urlMap.put(pattern, httpHandler);
                }
            }
        }
        WebSocketHandlerMapping hm = new WebSocketHandlerMapping();
        hm.setUrlMap(urlMap);
        hm.setOrder(this.order);
        if (this.urlPathHelper != null) {
            hm.setUrlPathHelper(this.urlPathHelper);
        }
        return hm;
    }
}
