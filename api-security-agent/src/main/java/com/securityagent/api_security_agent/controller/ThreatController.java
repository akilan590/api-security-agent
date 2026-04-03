package com.securityagent.api_security_agent.controller;

import com.securityagent.api_security_agent.model.ThreatLog;
import com.securityagent.api_security_agent.service.ThreatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/threat")
public class ThreatController {

    @Autowired
    private ThreatService threatService;

    // GET /threat/logs
    // returns all attack logs
    @GetMapping("/logs")
    public ResponseEntity<List<ThreatLog>> getAllLogs() {
        List<ThreatLog> logs =
                threatService.getAllThreats();
        return ResponseEntity.ok(logs);
    }

    // GET /threat/logs/{id}
    // returns single log by id
    @GetMapping("/logs/{id}")
    public ResponseEntity<?> getLogById(
            @PathVariable Long id) {

        Optional<ThreatLog> log =
                threatService.getThreatById(id);

        if (log.isPresent()) {
            return ResponseEntity.ok(log.get());
        }

        return ResponseEntity
                .status(404)
                .body("{\"message\": \"Log not found\"}");
    }

    // GET /threat/logs/type/{type}
    // filter logs by attack type
    // example: /threat/logs/type/SQL_INJECTION
    @GetMapping("/logs/type/{type}")
    public ResponseEntity<List<ThreatLog>> getByType(
            @PathVariable String type) {

        List<ThreatLog> logs =
                threatService.getThreatsByType(type);
        return ResponseEntity.ok(logs);
    }

    // GET /threat/logs/ip/{ip}
    // filter logs by IP address
    @GetMapping("/logs/ip/{ip}")
    public ResponseEntity<List<ThreatLog>> getByIp(
            @PathVariable String ip) {

        List<ThreatLog> logs =
                threatService.getThreatsByIp(ip);
        return ResponseEntity.ok(logs);
    }

    // GET /threat/latest
    // returns latest 10 attacks
    @GetMapping("/latest")
    public ResponseEntity<List<ThreatLog>> getLatest() {
        List<ThreatLog> logs =
                threatService.getLatestThreats();
        return ResponseEntity.ok(logs);
    }

    // GET /threat/stats
    // returns attack statistics
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats =
                threatService.getStats();
        return ResponseEntity.ok(stats);
    }

    // DELETE /threat/logs/{id}
    // deletes a single log
    @DeleteMapping("/logs/{id}")
    public ResponseEntity<String> deleteLog(
            @PathVariable Long id) {

        boolean deleted = threatService.deleteThreat(id);

        if (deleted) {
            return ResponseEntity.ok(
                    "Log deleted successfully");
        }

        return ResponseEntity
                .status(404)
                .body("Log not found");
    }

}