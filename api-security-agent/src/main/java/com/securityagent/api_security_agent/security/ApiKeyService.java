package com.securityagent.api_security_agent.security;

import org.springframework.stereotype.Service;

@Service
public class ApiKeyService {


    // in real projects store this in application.properties
    private static final String VALID_API_KEY =
            "myApiKey123";

    // checks if the provided key matches
    public boolean isValid(String apiKey) {
        if (apiKey == null) return false;
        return VALID_API_KEY.equals(apiKey);
    }

}