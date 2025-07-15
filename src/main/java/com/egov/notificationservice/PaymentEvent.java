package com.egov.notificationservice;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.annotation.Collation;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "payment-events")
public class PaymentEvent {
    @Id
    String id;
    String paymentId;
    String reservationId;
    String phone;
    PaymentStatus status;
}
