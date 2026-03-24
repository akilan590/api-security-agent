package com.securityagent.api_security_agent.agent;

import org.springframework.stereotype.Component;

@Component
public class RequestAnalyzer {

    public boolean detectSQLInjection(String input) {

        if (input == null) return false;

        String[] sqlPatterns = {
                "select",
                "insert",
                "update",
                "delete",
                "drop",
                "union",
                "truncate",
                "--",
                "or+1=1",
                "or%201=1",
                "1=1",
                "';",
                "/*",
                "*/"
        };

        input = input.toLowerCase();

        for (String pattern : sqlPatterns) {
            if (input.contains(pattern)) {
                return true;
            }
        }

        return false;
    }

    public boolean detectXSS(String input) {

        if (input == null) return false;

        String[] xssPatterns = {
                "<script>",
                "%3cscript",
                "</script>",
                "javascript:",
                "onerror=",
                "onload=",
                "onclick=",
                "<iframe>",
                "alert(",
                "document.cookie"
        };

        input = input.toLowerCase();

        for (String pattern : xssPatterns) {
            if (input.contains(pattern)) {
                return true;
            }
        }

        return false;
    }

}