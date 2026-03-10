package com.chat.chatapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Set;
import java.util.HashSet;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document(collection = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Room {
    @Id
    private String id; // Unique access code

    private String name;

    @DBRef
    private User owner;

    @DBRef
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<User> members = new HashSet<>();

    private boolean aiEnabled = false;
    private String icon;
    private String category;
    private String type = "COMMUNITY"; // COMMUNITY or GROUP
}
