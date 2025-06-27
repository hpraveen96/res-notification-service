package com.egov.commservice;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
public class Credential
{
    @Id
    String phone;
    String password;
    String type; // CUSTOMER, CONTRACTOR, LABOUR
}
