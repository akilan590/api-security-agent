package com.securityagent.api_security_agent.dto;


import lombok.Data;

@Data
public class LoginRequest {

    private String Username;
    private  String Password;
}
