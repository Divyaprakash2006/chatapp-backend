package com.chat.chatapp.controller;

import com.chat.chatapp.model.Room;
import com.chat.chatapp.model.User;
import com.chat.chatapp.repository.RoomRepository;
import com.chat.chatapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(originPatterns = "*")
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestParam String username, @RequestBody Room room) {
        Optional<User> owner = userRepository.findByUsername(username);
        if (owner.isEmpty())
            return ResponseEntity.badRequest().body("User not found");

        room.setOwner(owner.get());
        room.getMembers().add(owner.get());

        // Add additional members if provided (for Group creation)
        if (room.getMembers() != null) {
            // The members from request might only have IDs/usernames, but DBRef needs full
            // objects or specific handling.
            // For now, assume the frontend sends a set of usernames or we just save what's
            // there if they are valid User objects.
            // Usually, we'd look them up, but let's see if we can simplify.
        }

        roomRepository.save(room);
        return ResponseEntity.ok(room);
    }

    @PostMapping("/join/{id}")
    public ResponseEntity<?> joinRoom(@PathVariable String id, @RequestParam String username) {
        Optional<Room> roomOpt = roomRepository.findById(id);
        if (roomOpt.isEmpty())
            return ResponseEntity.badRequest().body("Room not found");

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty())
            return ResponseEntity.badRequest().body("User not found");

        Room room = roomOpt.get();
        room.getMembers().add(userOpt.get());
        roomRepository.save(room);
        return ResponseEntity.ok(room);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Room>> getMyRooms(@RequestParam String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty())
            return ResponseEntity.ok(List.of());
        return ResponseEntity.ok(roomRepository.findByMembersContaining(userOpt.get()));
    }

    @PostMapping("/{id}/toggle-ai")
    public ResponseEntity<?> toggleAi(@PathVariable String id) {
        Optional<Room> roomOpt = roomRepository.findById(id);
        if (roomOpt.isEmpty())
            return ResponseEntity.badRequest().body("Room not found");

        Room room = roomOpt.get();
        room.setAiEnabled(!room.isAiEnabled());
        roomRepository.save(room);
        return ResponseEntity.ok(room);
    }
}
