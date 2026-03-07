package com.chat.chatapp.controller;

import com.chat.chatapp.model.ChatMessage;
import com.chat.chatapp.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
