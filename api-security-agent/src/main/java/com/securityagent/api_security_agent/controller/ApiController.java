package com.securityagent.api_security_agent.controller;

import com.securityagent.api_security_agent.agent.ThreatDetector;
import com.securityagent.api_security_agent.model.ThreatLog;
import com.securityagent.api_security_agent.service.ThreatService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ApiController {

    @Autowired
    private ThreatDetector threatDetector;

    @Autowired
    private ThreatService threatService;

    @GetMapping("/api/test")
    public ResponseEntity<String> testApi() {
        return ResponseEntity.ok(
                "API Security Agent is running!");
    }

    @GetMapping("/api/scan")
    public ResponseEntity<String> scan(
            @RequestParam String input,
            HttpServletRequest request) {

        String result = threatDetector
                .detect(input);
        String ip = request.getRemoteAddr();
        String endpoint = request.getRequestURI();

        if (!result.equals("SAFE")) {
            threatService.logThreat(
                    ip, endpoint, result);
            return ResponseEntity
                    .status(403)
                    .body("BLOCKED: " + result);
        }

        return ResponseEntity.ok(
                "Input is safe: " + input);
    }

    @GetMapping("/api/logs")
    public ResponseEntity<List<ThreatLog>>
    getLogs() {
        return ResponseEntity.ok(
                threatService.getAllThreats());
    }

}