package com.chat.chatapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Set;
import java.util.HashSet;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Document(collection = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    private String id; // Unique access code

    private String name;

    @DBRef
    private User owner;

    @DBRef
    @JsonIgnore
    private Set<User> members = new HashSet<>();

    private boolean aiEnabled = false;
}
