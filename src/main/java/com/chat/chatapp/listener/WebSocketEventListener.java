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
    private org.springframework.messaging.simp.SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private UserRepository userRepository;

    @EventListener
    public void handleWebSocketConnectListener(org.springframework.web.socket.messaging.SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        // Extract username from connect headers
        java.util.Map<String, Object> nativeHeaders = (java.util.Map<String, Object>) headerAccessor.getMessageHeaders()
                .get("nativeHeaders");
        if (nativeHeaders != null && nativeHeaders.containsKey("username")) {
            String username = ((java.util.List<String>) nativeHeaders.get("username")).get(0);
            headerAccessor.getSessionAttributes().put("username", username);

            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setOnline(true);
                userRepository.save(user);
                broadcastStatus(username, true);
            }
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (username != null) {
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                User u = user.get();
                u.setOnline(false);
                userRepository.save(u);
                broadcastStatus(username, false);
            }
        }
    }

    private void broadcastStatus(String username, boolean online) {
        java.util.Map<String, Object> statusUpdate = new java.util.HashMap<>();
        statusUpdate.put("type", "STATUS_UPDATE");
        statusUpdate.put("sender", username);
        statusUpdate.put("online", online);
        statusUpdate.put("timestamp", java.time.LocalDateTime.now().toString());

        // Broadcast to all users (friends will filter on frontend)
        messagingTemplate.convertAndSend("/topic/status", (Object) statusUpdate);
    }
}
