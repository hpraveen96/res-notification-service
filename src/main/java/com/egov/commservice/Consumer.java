package com.egov.commservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.io.IOException;
import java.time.Instant;


@Service
public class Consumer
{
    private final Logger logger = LoggerFactory.getLogger(Consumer.class);

    @Autowired
    ApplicationContext ctx;

    @Autowired
    MessageRepository messageRepository;

    @KafkaListener(topics = "project-events", groupId = "ciw-project-events-consumer-comm-service")
    public void consumeProjectEvents(String message) throws IOException
    {
        //analytics_counter.increment();
        ObjectMapper mapper  = new ObjectMapper();
        ProjectEvent datum =  mapper.readValue(message, ProjectEvent.class);

        logger.info("Received ProjectEvent : " + datum);

        String[] trace =  datum.getTraceId().split("-");
        String traceId = trace[1];

        if(datum.getEventType().equals("FLOATED"))
        {
           // FIND ALL THE CONTRACTORS FROM THE AUTH SERVICE - A WEBFLUX CALL | include the traceId in the request header
            ctx.getBean("authGetUsersWebClient", WebClient.class)
                    .get()
                    .uri("/get/users?type=CONTRACTOR") // Assuming the endpoint to get contractors by role
                    .retrieve()
                    .bodyToFlux(Credential.class)
                    .subscribe(contractor -> {
                        // PREPARE AND SEND A MESSAGE FOR EACH CONTRACTOR - A DATABASE OPERATION
                        Message msg = new Message();
                        msg.setSenderId("ADMIN");
                        msg.setReceiverId(contractor.getPhone());
                        msg.setStatus("SENT");
                        msg.setTimestamp(Instant.now());
                        msg.setContent("NEW PROJECT FLOATED WITH ID: "+datum.getProjectId());
                        msg.setContext("PROJECT");
                        msg.setContextId(datum.getProjectId());

                        Message savedMessage =  messageRepository.save(msg);

                        logger.info("MESSAGE SUCCESSFULLY SAVED "+savedMessage.toString());
                    });
            // PREPARE AND SEND A MESSAGE FOR EACH CONTRACTOR - A DATABASE OPERATION
        }

    }

}

