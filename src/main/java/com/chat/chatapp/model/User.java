package com.chat.chatapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String password;

    @Indexed(unique = true)
    private String callingId;

    private java.time.LocalDateTime registrationTime;

    @DBRef
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<User> friends = new HashSet<>();

    @DBRef
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Room> joinedRooms = new HashSet<>();

    private Boolean online;

    private String bio;

    private String profilePicture;

    private Boolean aiEnabled = false;

    public boolean isAiEnabled() {
        return aiEnabled != null && aiEnabled;
    }

    public boolean isOnline() {
        return online != null && online;
    }
}
