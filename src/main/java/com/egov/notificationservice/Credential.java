package com.egov.notificationservice;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class Credential
{
    @Id
    String phone;
    String password;
    String type; // CUSTOMER, CONTRACTOR, LABOUR
}
