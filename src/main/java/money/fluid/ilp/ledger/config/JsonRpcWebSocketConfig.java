package money.fluid.ilp.ledger.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractJsonRpcWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableJsonRpcWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.JsonRpcEndpointRegistry;
import org.springframework.web.socket.config.annotation.JsonRpcWebSocketMessageBrokerConfigurer;

/**
 * Configuration for Ledger Websocket endpoints.
 *
 * @see "https://docs.spring.io/spring/docs/current/spring-framework-reference/html/websocket.html#websocket-server"
 */
@Configuration
@EnableJsonRpcWebSocketMessageBroker
@ComponentScan({
        "money.fluid.ilp.ledger.web.websocket.jsonrpc"
})
public class JsonRpcWebSocketConfig extends AbstractJsonRpcWebSocketMessageBrokerConfigurer implements JsonRpcWebSocketMessageBrokerConfigurer {

    @Override
    public void registerJsonRpcEndpoints(JsonRpcEndpointRegistry registry) {
        registry.addEndpoint("/websockets")
                // TODO: Adjust this?
                .setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic");

        // TODO: See https://github.com/fluid-money/ilp-ledger-java/issues/2
        //.setTaskScheduler(taskshceduler)

    }
}
