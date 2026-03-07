package com.chat.chatapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Document(collection = "chat_messages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {

    @Id
    private String id;

    private MessageType type;

    private String content;
    private String sender;
    private String recipient; // For private chats
    private String roomId; // For group/room chats
    private LocalDateTime timestamp;

    private String fileUrl;
    private String fileName;
    private String fileType;

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }
}