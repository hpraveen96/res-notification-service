package com.egov.commservice;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "messages")
@Getter
@Setter
public class Message
{
    @Id
    String id; // unique identifier for the message
    String senderId; // phone number of the sender
    String receiverId;
    String context;
    String contextId; // id of the context, e.g., complaint id, task id, etc.
    Instant timestamp; // when the message was sent
    String status; // SENT, DELIVERED, READ
    String content; // the actual message content, can be text or media URL

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", context='" + context + '\'' +
                ", contextId='" + contextId + '\'' +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
