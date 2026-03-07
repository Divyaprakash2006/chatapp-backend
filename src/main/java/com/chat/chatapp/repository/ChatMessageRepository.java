package com.chat.chatapp.repository;

import com.chat.chatapp.model.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByRoomIdOrderByTimestampAsc(String roomId);

    @Query("{ '$or': [ { 'sender': ?0, 'recipient': ?1 }, { 'sender': ?1, 'recipient': ?0 } ] }")
    List<ChatMessage> findPrivateHistory(String user1, String user2);

    void deleteByRoomId(String roomId);

    @Query(value = "{ '$or': [ { 'sender': ?0, 'recipient': ?1 }, { 'sender': ?1, 'recipient': ?0 } ] }", delete = true)
    void deletePrivateHistory(String user1, String user2);
}