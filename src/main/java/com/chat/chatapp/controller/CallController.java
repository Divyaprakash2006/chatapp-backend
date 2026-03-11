package com.chat.chatapp.controller;

import com.chat.chatapp.model.CallLog;
import com.chat.chatapp.repository.CallLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/calls")
@CrossOrigin(originPatterns = "*")
public class CallController {

    @Autowired
    private CallLogRepository callLogRepository;

    @PostMapping
    public ResponseEntity<CallLog> saveCallLog(@RequestBody CallLog callLog) {
        if (callLog.getTimestamp() == null) {
            callLog.setTimestamp(LocalDateTime.now());
        }
        return ResponseEntity.ok(callLogRepository.save(callLog));
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<CallLog>> getCallHistory(@PathVariable String username) {
        return ResponseEntity.ok(callLogRepository.findByCallerOrReceiverOrderByTimestampDesc(username, username));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> clearCallHistory(@PathVariable String username) {
        callLogRepository.deleteByCallerOrReceiver(username, username);
        return ResponseEntity.ok().build();
    }
}
