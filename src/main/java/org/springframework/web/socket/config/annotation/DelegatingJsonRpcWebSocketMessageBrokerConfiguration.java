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

package org.springframework.web.socket.config.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link JsonRpcWebSocketMessageBrokerConfigurationSupport} extension that detects
 * beans of type {@link JsonRpcWebSocketMessageBrokerConfigurer} and delegates to all
 * of them allowing callback style customization of the configuration provided
 * in {@link JsonRpcWebSocketMessageBrokerConfigurationSupport}.
 * <p>
 * <p>This class is typically imported via {@link EnableJsonRpcWebSocketMessageBroker}.
 */
@Configuration
public class DelegatingJsonRpcWebSocketMessageBrokerConfiguration extends JsonRpcWebSocketMessageBrokerConfigurationSupport {

    // TODO: Refactor using Streams...

    private final List<JsonRpcWebSocketMessageBrokerConfigurer> configurers = new ArrayList<>();

    @Autowired(required = false)
    public void setConfigurers(List<JsonRpcWebSocketMessageBrokerConfigurer> configurers) {
        if (!CollectionUtils.isEmpty(configurers)) {
            this.configurers.addAll(configurers);
        }
    }

    @Override
    protected void registerJsonRpcEndpoints(JsonRpcEndpointRegistry registry) {
        for (JsonRpcWebSocketMessageBrokerConfigurer configurer : this.configurers) {
            configurer.registerJsonRpcEndpoints(registry);
        }
    }

    @Override
    protected void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        for (JsonRpcWebSocketMessageBrokerConfigurer configurer : this.configurers) {
            configurer.configureWebSocketTransport(registration);
        }
    }


    @Override
    protected void configureClientInboundChannel(ChannelRegistration registration) {
        for (JsonRpcWebSocketMessageBrokerConfigurer configurer : this.configurers) {
            configurer.configureClientInboundChannel(registration);
        }
    }

    @Override
    protected void configureClientOutboundChannel(ChannelRegistration registration) {
        for (JsonRpcWebSocketMessageBrokerConfigurer configurer : this.configurers) {
            configurer.configureClientOutboundChannel(registration);
        }
    }

    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        for (JsonRpcWebSocketMessageBrokerConfigurer configurer : this.configurers) {
            configurer.addArgumentResolvers(argumentResolvers);
        }
    }

    @Override
    protected void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        for (JsonRpcWebSocketMessageBrokerConfigurer configurer : this.configurers) {
            configurer.addReturnValueHandlers(returnValueHandlers);
        }
    }

    @Override
    protected boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        boolean registerDefaults = true;
        for (JsonRpcWebSocketMessageBrokerConfigurer configurer : this.configurers) {
            if (!configurer.configureMessageConverters(messageConverters)) {
                registerDefaults = false;
            }
        }
        return registerDefaults;
    }

    @Override
    protected void configureMessageBroker(MessageBrokerRegistry registry) {
        for (JsonRpcWebSocketMessageBrokerConfigurer configurer : this.configurers) {
            configurer.configureMessageBroker(registry);
        }
    }

}
