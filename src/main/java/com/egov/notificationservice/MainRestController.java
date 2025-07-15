package com.egov.notificationservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1")
public class MainRestController
{
    private static final Logger logger = LoggerFactory.getLogger(MainRestController.class);

    @Autowired
    PaymentEventService paymentEventService;

    @GetMapping("paymentevents/{paymentId}")
    public ResponseEntity<PaymentEvent> getById(@PathVariable String paymentId) {
        return ResponseEntity.ok((PaymentEvent) paymentEventService.getByPaymentId(paymentId));
    }

    @GetMapping("paymentevents/reservation/{reservationId}")
    public ResponseEntity<List<PaymentEvent>> getByReservation(@PathVariable String reservationId) {
        return ResponseEntity.ok(paymentEventService.getByReservation(reservationId));
    }



}
