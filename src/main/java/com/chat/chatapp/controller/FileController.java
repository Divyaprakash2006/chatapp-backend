package com.chat.chatapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(originPatterns = "*")
public class FileController {

    private final Path root = Paths.get("uploads").toAbsolutePath().normalize();

    public FileController() {
        try {
            Files.createDirectories(root);
            System.out.println("Uploads directory initialized at: " + root);
        } catch (IOException e) {
            System.err.println("Could not initialize storage: " + e.getMessage());
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            String originalFileName = file.getOriginalFilename();
            String cleanFileName = originalFileName != null ? originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_")
                    : "unnamed";
            String fileName = UUID.randomUUID().toString() + "_" + cleanFileName;

            Files.copy(file.getInputStream(), this.root.resolve(fileName));

            Map<String, String> response = new HashMap<>();
            response.put("fileUrl", "/api/files/download/" + fileName);
            response.put("fileName", originalFileName);
            response.put("fileType", file.getContentType());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // Print log to console for debugging
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        try {
            Path file = root.resolve(fileName);
            byte[] fileContent = Files.readAllBytes(file);
            return ResponseEntity.ok()
                    .header("Content-Type", Files.probeContentType(file))
                    .body(fileContent);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
