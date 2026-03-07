package com.chat.chatapp.controller;

import com.chat.chatapp.model.User;
import com.chat.chatapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/friends")
@CrossOrigin(originPatterns = "*")
public class FriendController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add/{friendUsername}")
    public ResponseEntity<?> addFriend(@RequestParam String username, @PathVariable String friendUsername) {
        if (username.equals(friendUsername))
            return ResponseEntity.badRequest().body("Cannot add yourself");

        Optional<User> userOpt = userRepository.findByUsername(username);
        Optional<User> friendOpt = userRepository.findByUsername(friendUsername);

        if (userOpt.isEmpty() || friendOpt.isEmpty())
            return ResponseEntity.badRequest().body("User not found");

        User user = userOpt.get();
        User friend = friendOpt.get();

        if (user.getFriends() == null) {
            user.setFriends(new java.util.HashSet<>());
        }

        user.getFriends().add(friend);
        userRepository.save(user);

        return ResponseEntity.ok("Friend added");
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyFriends(@RequestParam String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty())
            return ResponseEntity.ok(Set.of());

        if (userOpt.get().getFriends() == null) {
            return ResponseEntity.ok(java.util.Set.of());
        }

        return ResponseEntity.ok(userOpt.get().getFriends().stream().map(f -> {
            f.setPassword(null);
            return f;
        }).collect(Collectors.toSet()));
    }
}
