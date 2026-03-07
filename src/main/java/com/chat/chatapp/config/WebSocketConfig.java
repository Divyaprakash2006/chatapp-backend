package com.chat.chatapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Allowing "null" origin explicitly for local file execution and "*" for
        // general patterns
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // /topic = Public Rooms | /queue = Private Messages & Video Calls
        registry.enableSimpleBroker("/topic", "/queue");

        // Prefix for messages sent FROM frontend TO backend
        registry.setApplicationDestinationPrefixes("/app");

        // Prefix used to route messages to specific users
        registry.setUserDestinationPrefix("/user");
    }
}