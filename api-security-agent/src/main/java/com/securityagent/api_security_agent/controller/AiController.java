package com.securityagent.api_security_agent.controller;

import com.securityagent.api_security_agent.agent.ThreatDetector;
import com.securityagent.api_security_agent.model.AiAnalysisLog;
import com.securityagent.api_security_agent.service.AiAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Autowired
    private ThreatDetector threatDetector;

    @Autowired
    private AiAnalysisService aiAnalysisService;

    // POST /ai/analyze
    // manually send input for AI analysis
    @PostMapping("/analyze")
    public ResponseEntity<Map<String, String>> analyze(
            @RequestBody Map<String, String> body) {

        String input = body.get("input");

        if (input == null || input.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error", "input is required"));
        }

        String result = threatDetector
                .detect(input, "manual");

        return ResponseEntity.ok(Map.of(
                "input", input,
                "result", result,
                "analyzedBy", result.equals("SAFE")
                        ? "rules+AI" : "detected"
        ));
    }

    // GET /ai/logs
    // get all AI analysis logs
    @GetMapping("/logs")
    public ResponseEntity<List<AiAnalysisLog>>
    getAllLogs() {
        return ResponseEntity.ok(
                aiAnalysisService.getAllLogs());
    }

    // GET /ai/logs/latest
    // get latest AI analysis logs
    @GetMapping("/logs/latest")
    public ResponseEntity<List<AiAnalysisLog>>
    getLatestLogs() {
        return ResponseEntity.ok(
                aiAnalysisService.getLatestLogs());
    }

    // GET /ai/logs/threats
    // get logs where AI caught something
    @GetMapping("/logs/threats")
    public ResponseEntity<List<AiAnalysisLog>>
    getAiThreats() {
        return ResponseEntity.ok(
                aiAnalysisService.getAiCaughtThreats());
    }

}