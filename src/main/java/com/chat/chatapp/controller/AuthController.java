package com.chat.chatapp.controller;

import com.chat.chatapp.model.User;
import com.chat.chatapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(originPatterns = "*") // Allow frontend to access, including local files
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        System.out.println("Registration request for: " + user);
        // Validation
        if (user.getUsername() == null || !user.getUsername().matches("^[a-zA-Z0-9_]{3,20}$")) {
            return ResponseEntity.badRequest()
                    .body("Username must be 3-20 characters and alphanumeric (underscores allowed)");
        }
        if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return ResponseEntity.badRequest().body("Invalid email format");
        }
        if (user.getPassword() == null || !user.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")) {
            return ResponseEntity.badRequest()
                    .body("Password must be at least 8 characters and contain at least one letter and one digit");
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        // Secure password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Store callingId automatically from email
        user.setCallingId(user.getEmail());
        user.setRegistrationTime(java.time.LocalDateTime.now());

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody java.util.Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (username == null || password == null) {
            return ResponseEntity.status(401).body("Username and password are required");
        }

        Optional<User> dbUser = userRepository.findByUsername(username);
        if (dbUser.isPresent() && passwordEncoder.matches(password, dbUser.get().getPassword())) {
            User authenticatedUser = dbUser.get();
            authenticatedUser.setOnline(true);
            userRepository.save(authenticatedUser);
            authenticatedUser.setPassword(null); // Security: don't send password back
            return ResponseEntity.ok(authenticatedUser);
        }
        return ResponseEntity.status(401).body("Invalid username or password");
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam String query) {
        // Simple search that returns users whose username contains the query
        // In a real app, we'd use a custom query in repository, but JPA's findAll
        // and filtering works for small datasets.
        return ResponseEntity.ok(userRepository.findAll().stream()
                .filter(u -> u.getUsername().toLowerCase().contains(query.toLowerCase()) ||
                        (u.getCallingId() != null && u.getCallingId().toLowerCase().contains(query.toLowerCase())))
                .map(u -> {
                    u.setPassword(null); // Security: don't send passwords back
                    return u;
                })
                .toList());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            User u = user.get();
            u.setOnline(false);
            userRepository.save(u);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/toggle-ai")
    public ResponseEntity<?> toggleAi(@RequestParam String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty())
            return ResponseEntity.badRequest().body("User not found");

        User user = userOpt.get();
        user.setAiEnabled(!user.isAiEnabled());
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody User profileData) {
        Optional<User> userOpt = userRepository.findByUsername(profileData.getUsername());
        if (userOpt.isEmpty())
            return ResponseEntity.badRequest().body("User not found");

        User user = userOpt.get();
        if (profileData.getBio() != null)
            user.setBio(profileData.getBio());
        if (profileData.getProfilePicture() != null)
            user.setProfilePicture(profileData.getProfilePicture());

        userRepository.save(user);
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }
}
