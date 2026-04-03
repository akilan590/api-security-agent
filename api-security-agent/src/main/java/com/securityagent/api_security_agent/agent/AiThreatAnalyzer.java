package com.securityagent.api_security_agent.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class AiThreatAnalyzer {

    private final ChatClient chatClient;

    // Spring AI automatically injects ChatClient
    public AiThreatAnalyzer(
            ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String analyze(String input) {

        // build prompt for AI
        String prompt = """
            You are a cybersecurity expert.
            Your job is to analyze inputs for
            security threats.

            Analyze this input carefully: %s

            Reply with ONLY one of these words:
            SAFE
            SQL_INJECTION
            XSS_ATTACK
            COMMAND_INJECTION
            PATH_TRAVERSAL
            UNKNOWN_ATTACK

            Rules:
            - Reply with ONE word only
            - No explanation
            - No punctuation
            - No extra text
            """.formatted(input);

        try {
            String result = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            System.out.println(
                    "AI analysis for input: " + input);
            System.out.println(
                    "AI result: " + result);

            // clean the result
            return result.trim().toUpperCase();

        } catch (Exception e) {
            System.out.println(
                    "AI analysis failed: " + e.getMessage());
            // if AI fails, return safe
            // so app keeps working
            return "SAFE";
        }
    }

}