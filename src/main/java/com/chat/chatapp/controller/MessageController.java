package com.chat.chatapp.controller;

import com.chat.chatapp.model.ChatMessage;
import com.chat.chatapp.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(originPatterns = "*")
public class MessageController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @GetMapping("/room/{roomId}")
    public List<ChatMessage> getRoomHistory(@PathVariable String roomId) {
        return chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId);
    }

    @GetMapping("/private")
    public List<ChatMessage> getPrivateHistory(@RequestParam String user1, @RequestParam String user2) {
        return chatMessageRepository.findPrivateHistory(user1, user2);
    }

    @DeleteMapping("/clear/room/{roomId}")
    public ResponseEntity<?> clearRoomHistory(@PathVariable String roomId) {
        chatMessageRepository.deleteByRoomId(roomId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clear/private")
    public ResponseEntity<?> clearPrivateHistory(@RequestParam String user1, @RequestParam String user2) {
        chatMessageRepository.deletePrivateHistory(user1, user2);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<?> deleteMessage(@RequestParam String timestamp, @RequestParam String sender) {
        try {
            // Frontend sends ISO strings like 2026-03-10T06:54:15.542Z or
            // 2026-03-10T06:54:15.542
            LocalDateTime ts;
            if (timestamp.contains("Z") || timestamp.contains("+")) {
                ts = java.time.OffsetDateTime.parse(timestamp).toLocalDateTime();
            } else {
                ts = LocalDateTime.parse(timestamp);
            }
            chatMessageRepository.deleteBySenderAndTimestamp(sender, ts);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Delete error: " + e.getMessage());
            return ResponseEntity.badRequest().body("Invalid timestamp format: " + e.getMessage());
        }
    }
}
