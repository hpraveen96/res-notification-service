package com.egov.notificationservice;

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
    PaymentEventRepository paymentEventRepository;

    @KafkaListener(topics = "payment-events", groupId = "res-payment-events-consumer-notification-service")
    public void consumePaymentEvents(String message) throws IOException
    {
        //analytics_counter.increment();
        ObjectMapper mapper  = new ObjectMapper();
        PaymentEvent datum =  mapper.readValue(message, PaymentEvent.class);

        logger.info("Received ProjectEvent : " + datum);

            PaymentEvent paymentEvent = new PaymentEvent();
            paymentEvent.setPaymentId(datum.getPaymentId());
            paymentEvent.setPhone(datum.getPhone());
            paymentEvent.setStatus(datum.getStatus());
            paymentEvent.setReservationId(datum.getReservationId());
            PaymentEvent savedPaymentEvent =  paymentEventRepository.save(paymentEvent);
            // PREPARE AND SEND A MESSAGE FOR EACH CONTRACTOR - A DATABASE OPERATION
    }

}

