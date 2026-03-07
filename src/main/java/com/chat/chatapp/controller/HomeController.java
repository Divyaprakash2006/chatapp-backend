package com.chat.chatapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> healthCheck() {
        return Map.of(
                "status", "UP",
                "message", "ChatApp Backend is running on Render",
                "version", "0.0.1-SNAPSHOT");
    }
}
