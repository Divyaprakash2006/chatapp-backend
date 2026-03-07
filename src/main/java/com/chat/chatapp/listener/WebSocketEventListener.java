package com.chat.chatapp.listener;

import com.chat.chatapp.model.User;
import com.chat.chatapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Optional;

@Component
public class WebSocketEventListener {

    @Autowired
    private UserRepository userRepository;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // Note: In a production app, we would store the username in the session
        // attributes
        // during connection to retrieve it here. For this implementation, we can handle
        // it
        // if user explicitly logs out or if we add session attributes in
        // WebSocketConfig.

        // Simple approach: Any user object that was marked online but hasn't had
        // heartbeat
        // could be cleaned up. For now, we'll focus on explicit logout and basic
        // disconnect handling.
    }
}
