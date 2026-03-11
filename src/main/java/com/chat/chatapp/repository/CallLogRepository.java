package com.chat.chatapp.repository;

import com.chat.chatapp.model.CallLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CallLogRepository extends MongoRepository<CallLog, String> {
    List<CallLog> findByCallerOrderByTimestampDesc(String caller);

    List<CallLog> findByReceiverOrderByTimestampDesc(String receiver);

    List<CallLog> findByCallerOrReceiverOrderByTimestampDesc(String caller, String receiver);

    void deleteByCallerOrReceiver(String caller, String receiver);
}
