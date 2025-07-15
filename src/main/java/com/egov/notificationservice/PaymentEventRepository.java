package com.egov.notificationservice;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentEventRepository extends MongoRepository<PaymentEvent, String>
{

    List<PaymentEvent> findByReservationId(String reservationId);

    List<PaymentEvent> findByPaymentId(String paymentId);
}
