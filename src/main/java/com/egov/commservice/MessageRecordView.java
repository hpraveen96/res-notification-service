package com.egov.commservice;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRecordView {

    String context;
    String contextId;
    String messageId;

    @Override
    public String toString() {
        return "MessageRecordView{" +
                "context='" + context + '\'' +
                ", contextId='" + contextId + '\'' +
                ", messageId='" + messageId + '\'' +
                '}';
    }
}
