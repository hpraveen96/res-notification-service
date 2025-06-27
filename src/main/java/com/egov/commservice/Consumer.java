package com.egov.commservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.converters.Auto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@Service
public class Consumer
{
    private final Logger logger = LoggerFactory.getLogger(Consumer.class);

    @Autowired
    ApplicationContext ctx;

    @KafkaListener(topics = "project-events", groupId = "ciw-project-events-consumer-comm-service")
    public void consumeProjectEvents(String message) throws IOException
    {
        //analytics_counter.increment();
        ObjectMapper mapper  = new ObjectMapper();
        ProjectEvent datum =  mapper.readValue(message, ProjectEvent.class);

        if(datum.getEventType().equals("FLOATED"))
        {
           // FIND ALL THE CONTRACTORS FROM THE AUTH SERVICE - A WEBFLUX CALL | include the traceId in the request header
            ctx.getBean("contractorServiceWebClient", WebClient.class)
                    .get()
                    .uri("/get/users?role=CONTRACTOR") // Assuming the endpoint to get contractors by role
                    .header("traceId", datum.getTraceId())
                    .retrieve()
                    .bodyToFlux(Credential.class)
                    .subscribe(contractor -> {
                        // PREPARE AND SEND A MESSAGE FOR EACH CONTRACTOR - A DATABASE OPERATION
                    });

            // PREPARE AND SEND A MESSAGE FOR EACH CONTRACTOR - A DATABASE OPERATION

        }

    }
}

