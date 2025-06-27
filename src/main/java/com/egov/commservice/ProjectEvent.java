package com.egov.commservice;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectEvent
{
    String projectId;
    String eventType; // CREATED, UPDATED, DELETED
    String traceId; // For tracing the event in logs


    @Override
    public String toString()
    {
        return "ProjectEvent{" +
                "projectId='" + projectId + '\'' +
                ", eventType='" + eventType + '\'' +
                '}';
    }

}
