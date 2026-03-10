package com.chat.chatapp.controller;

import com.chat.chatapp.model.ChatMessage;
import com.chat.chatapp.model.Room;
import com.chat.chatapp.model.SignalMessage;
import com.chat.chatapp.model.User;
import com.chat.chatapp.repository.ChatMessageRepository;
import com.chat.chatapp.repository.RoomRepository;
import com.chat.chatapp.repository.UserRepository;
import com.chat.chatapp.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AIService aiService;

    // 1. Handle Room/Group Chat
    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/{roomId}")
    public ChatMessage sendToRoom(@DestinationVariable String roomId, @Payload ChatMessage chatMessage) {
        System.out.println("[ROOM WEB-SOCKET] Received from: " + chatMessage.getSender() + " to Room: " + roomId);
        if (chatMessage.getTimestamp() == null) {
            chatMessage.setTimestamp(LocalDateTime.now());
        }
        chatMessage.setRoomId(roomId);

        // Save to Database
        if (chatMessage.getFileUrl() != null) {
            System.out.println("Processing file message - Name: " + chatMessage.getFileName() + ", URL: "
                    + chatMessage.getFileUrl());
        }
        chatMessageRepository.save(chatMessage);

        // Check if AI is enabled for this room
        Optional<Room> roomOpt = roomRepository.findById(roomId);
        if (roomOpt.isPresent() && roomOpt.get().isAiEnabled()) {
            aiService.getAIResponse(chatMessage.getContent()).thenAccept(response -> {
                ChatMessage aiMessage = new ChatMessage();
                aiMessage.setSender("AI Assistant");
                aiMessage.setContent(response);
                aiMessage.setRoomId(roomId);
                aiMessage.setType(ChatMessage.MessageType.CHAT);
                aiMessage.setTimestamp(LocalDateTime.now());

                // Save and Broadcast AI message
                System.out.println("[AI-RESPONSE] Sending AI message to Room: " + roomId);
                chatMessageRepository.save(aiMessage);
                messagingTemplate.convertAndSend("/topic/" + roomId, aiMessage);
            });
        }

        // Broadcast original message to everyone in /topic/{roomId}
        return chatMessage;
    }

    // 2. Handle Private Text Chat
    @MessageMapping("/chat/private")
    public void sendPrivateMessage(@Payload ChatMessage chatMessage) {
        System.out.println("[PRIVATE WEB-SOCKET] Received from: " + chatMessage.getSender() + " to: "
                + chatMessage.getRecipient());
        if (chatMessage.getTimestamp() == null) {
            chatMessage.setTimestamp(LocalDateTime.now());
        }

        // Save to Database
        if (chatMessage.getFileUrl() != null) {
            System.out.println("Processing private file - Name: " + chatMessage.getFileName() + ", Recipient: "
                    + chatMessage.getRecipient());
        }
        chatMessageRepository.save(chatMessage);

        // Send to the recipient's personal topic
        messagingTemplate.convertAndSend(
                "/topic/messages/" + chatMessage.getRecipient(),
                chatMessage);

        // Check if the recipient is the dedicated Gemini AI
        if ("Gemini AI".equals(chatMessage.getRecipient())) {
            aiService.getAIResponse(chatMessage.getContent()).thenAccept(response -> {
                ChatMessage aiMessage = new ChatMessage();
                aiMessage.setSender("Gemini AI");
                aiMessage.setContent(response);
                aiMessage.setRecipient(chatMessage.getSender());
                aiMessage.setType(ChatMessage.MessageType.CHAT);
                aiMessage.setTimestamp(LocalDateTime.now());

                System.out.println("[AI-RESPONSE] Sending Dedicated Gemini response to: " + chatMessage.getSender());
                chatMessageRepository.save(aiMessage);
                messagingTemplate.convertAndSend("/topic/messages/" + chatMessage.getSender(), aiMessage);
            });
            return; // Don't proceed to recipient toggle check
        }

        // Check if the recipient has AI enabled
        Optional<User> recipientOpt = userRepository.findByUsername(chatMessage.getRecipient());
        if (recipientOpt.isPresent() && recipientOpt.get().isAiEnabled()) {
            aiService.getAIResponse(chatMessage.getContent()).thenAccept(response -> {
                ChatMessage aiMessage = new ChatMessage();
                aiMessage.setSender("AI Assistant");
                aiMessage.setContent(response);
                aiMessage.setRecipient(chatMessage.getSender()); // Send back to sender
                aiMessage.setType(ChatMessage.MessageType.CHAT);
                aiMessage.setTimestamp(LocalDateTime.now());

                // Save to Database
                chatMessageRepository.save(aiMessage);

                // Send to the original sender's personal topic
                messagingTemplate.convertAndSend(
                        "/topic/messages/" + chatMessage.getSender(),
                        aiMessage);
            });
        }
    }

    // 3. Handle WebRTC Video/Audio Call Signaling
    @MessageMapping("/signal")
    public void processSignaling(@Payload SignalMessage signalMessage) {
        System.out.println("[SIGNALING] Route from: " + signalMessage.getSender() + " to: "
                + signalMessage.getRecipient() + " Type: " + signalMessage.getType());
        // Route to recipient's personal signaling topic
        messagingTemplate.convertAndSend(
                "/topic/signal/" + signalMessage.getRecipient(),
                signalMessage);
    }
}