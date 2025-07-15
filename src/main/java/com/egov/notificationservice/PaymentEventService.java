package com.egov.notificationservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentEventService {

    @Autowired
    PaymentEventRepository paymentEventRepository;

    public List<PaymentEvent> getByPaymentId(String paymentId) {
        return paymentEventRepository.findByPaymentId(paymentId);
    }

    public List<PaymentEvent> getByReservation(String reservationId) {
        return paymentEventRepository.findByReservationId(reservationId);
    }


}
