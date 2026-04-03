package com.securityagent.api_security_agent.service;

import com.securityagent.api_security_agent.model.AiAnalysisLog;
import com.securityagent.api_security_agent.repository.AiAnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AiAnalysisService {

    @Autowired
    private AiAnalysisRepository aiAnalysisRepository;

    // save AI analysis result to database
    public void logAnalysis(
            String input,
            String ruleResult,
            String aiResult,
            String finalDecision,
            String ip) {

        AiAnalysisLog log = new AiAnalysisLog();
        log.setInput(input);
        log.setRuleResult(ruleResult);
        log.setAiResult(aiResult);
        log.setFinalDecision(finalDecision);
        log.setIpAddress(ip);
        log.setTimestamp(LocalDateTime.now());

        aiAnalysisRepository.save(log);

        System.out.println(
                "AI Analysis logged: " +
                        "rules=" + ruleResult +
                        " ai=" + aiResult +
                        " final=" + finalDecision);
    }

    // get all AI analysis logs
    public List<AiAnalysisLog> getAllLogs() {
        return aiAnalysisRepository.findAll();
    }

    // get latest analysis
    public List<AiAnalysisLog> getLatestLogs() {
        return aiAnalysisRepository
                .findLatestAnalysis();
    }

    // get logs where AI caught something
    public List<AiAnalysisLog> getAiCaughtThreats() {
        return aiAnalysisRepository
                .findByFinalDecision("AI_DETECTED");
    }

}