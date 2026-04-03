package com.securityagent.api_security_agent.agent;

import com.securityagent.api_security_agent.service.AiAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ThreatDetector {

    @Autowired
    private RequestAnalyzer analyzer;

    @Autowired
    private AiThreatAnalyzer aiThreatAnalyzer;

    @Autowired
    private AiAnalysisService aiAnalysisService;

    public String detect(String input) {
        return detect(input, "unknown");
    }

    public String detect(String input, String ip) {

        // Step 1 - rule based check first
        // this is fast and catches known attacks
        String ruleResult = "SAFE";

        if (analyzer.detectSQLInjection(input)) {
            ruleResult = "SQL_INJECTION";
        } else if (analyzer.detectXSS(input)) {
            ruleResult = "XSS_ATTACK";
        }

        // Step 2 - if rules caught something
        // block immediately, no need for AI
        if (!ruleResult.equals("SAFE")) {
            System.out.println(
                    "Rule detected: " + ruleResult);

            // log that rules caught this
            aiAnalysisService.logAnalysis(
                    input,
                    ruleResult,
                    "NOT_CALLED",
                    ruleResult,
                    ip);

            return ruleResult;
        }

        // Step 3 - rules say safe
        // now ask AI for deeper analysis
        System.out.println(
                "Rules passed. Asking AI...");

        String aiResult =
                aiThreatAnalyzer.analyze(input);

        // Step 4 - AI found something
        if (!aiResult.equals("SAFE")) {
            System.out.println(
                    "AI detected: " + aiResult);

            // log that AI caught this
            aiAnalysisService.logAnalysis(
                    input,
                    "SAFE",
                    aiResult,
                    "AI_DETECTED",
                    ip);

            return aiResult;
        }

        // Step 5 - both rules and AI say safe
        System.out.println(
                "Both rules and AI say SAFE");

        aiAnalysisService.logAnalysis(
                input,
                "SAFE",
                "SAFE",
                "SAFE",
                ip);

        return "SAFE";
    }

}