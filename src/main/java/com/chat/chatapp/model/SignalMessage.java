package com.chat.chatapp.model;

import lombok.Data;

@Data
public class SignalMessage {
    private String sender;
    private String recipient;
    private String type; // e.g., "offer", "answer", "ice-candidate", "decline", "hangup"
    private String callType; // "audio" or "video"
    private Object data; // The WebRTC payload
}