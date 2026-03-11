package com.chat.chatapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "call_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallLog {
    @Id
    private String id;
    private String caller;
    private String receiver;
    private String type; // AUDIO, VIDEO
    private String status; // COMPLETED, MISSED, DECLINED
    private String direction; // INCOMING, OUTGOING
    private LocalDateTime timestamp;
    private Long duration; // in seconds
}
