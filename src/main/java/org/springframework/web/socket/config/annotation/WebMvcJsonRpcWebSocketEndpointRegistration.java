package org.springframework.web.socket.config.annotation;

import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.OriginHandshakeInterceptor;
import org.springframework.web.socket.server.support.WebSocketHttpRequestHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An abstract base class for configuring JsonRpc over WebSocket endpoints.
 */
public class WebMvcJsonRpcWebSocketEndpointRegistration implements JsonRpcWebSocketEndpointRegistration {

    private final String[] paths;

    private final WebSocketHandler webSocketHandler;

    private HandshakeHandler handshakeHandler;

    private final List<HandshakeInterceptor> interceptors = new ArrayList<>();

    private final List<String> allowedOrigins = new ArrayList<>();

    public WebMvcJsonRpcWebSocketEndpointRegistration(final String[] paths, final WebSocketHandler webSocketHandler) {
        Assert.notEmpty(paths, "No paths specified");
        Assert.notNull(webSocketHandler, "WebSocketHandler must not be null");

        this.paths = paths;
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public JsonRpcWebSocketEndpointRegistration setHandshakeHandler(HandshakeHandler handshakeHandler) {
        Assert.notNull(handshakeHandler, "'handshakeHandler' must not be null");
        this.handshakeHandler = handshakeHandler;
        return this;
    }

    @Override
    public JsonRpcWebSocketEndpointRegistration addInterceptors(HandshakeInterceptor... interceptors) {
        if (!ObjectUtils.isEmpty(interceptors)) {
            this.interceptors.addAll(Arrays.asList(interceptors));
        }
        return this;
    }

    @Override
    public JsonRpcWebSocketEndpointRegistration setAllowedOrigins(String... allowedOrigins) {
        this.allowedOrigins.clear();
        if (!ObjectUtils.isEmpty(allowedOrigins)) {
            this.allowedOrigins.addAll(Arrays.asList(allowedOrigins));
        }
        return this;
    }

    protected HandshakeInterceptor[] getInterceptors() {
        List<HandshakeInterceptor> interceptors = new ArrayList<HandshakeInterceptor>();
        interceptors.addAll(this.interceptors);
        interceptors.add(new OriginHandshakeInterceptor(this.allowedOrigins));
        return interceptors.toArray(new HandshakeInterceptor[interceptors.size()]);
    }

    public final MultiValueMap<HttpRequestHandler, String> getMappings() {
        MultiValueMap<HttpRequestHandler, String> mappings = new LinkedMultiValueMap<HttpRequestHandler, String>();

        for (String path : this.paths) {
            WebSocketHttpRequestHandler handler;
            if (this.handshakeHandler != null) {
                handler = new WebSocketHttpRequestHandler(this.webSocketHandler, this.handshakeHandler);
            } else {
                handler = new WebSocketHttpRequestHandler(this.webSocketHandler);
            }
            HandshakeInterceptor[] interceptors = getInterceptors();
            if (interceptors.length > 0) {
                handler.setHandshakeInterceptors(Arrays.asList(interceptors));
            }
            mappings.add(handler, path);
        }

        return mappings;
    }

}
