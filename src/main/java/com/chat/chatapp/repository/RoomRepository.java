package com.chat.chatapp.repository;

import com.chat.chatapp.model.Room;
import com.chat.chatapp.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface RoomRepository extends MongoRepository<Room, String> {
    List<Room> findByMembersContaining(User user);

    List<Room> findByOwner(User owner);
}
