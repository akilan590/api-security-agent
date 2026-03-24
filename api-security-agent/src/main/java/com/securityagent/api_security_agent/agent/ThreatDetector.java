package com.securityagent.api_security_agent.agent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ThreatDetector {

    @Autowired
    private RequestAnalyzer requestAnalyzer;

    public String detect(String input) {

        if (input == null) return "SAFE";

        if (requestAnalyzer.detectSQLInjection(input)) {
            return "SQL Injection";
        }

        if (requestAnalyzer.detectXSS(input)) {
            return "XSS Attack";
        }

        return "SAFE";
    }

}