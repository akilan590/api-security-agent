package com.securityagent.api_security_agent.service;

import com.securityagent.api_security_agent.model.ThreatLog;
import com.securityagent.api_security_agent.repository.ThreatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ThreatService {

    @Autowired
    private ThreatRepository threatRepository;

    // save attack log to database
    public void logThreat(
            String ip,
            String endpoint,
            String attackType) {

        ThreatLog log = new ThreatLog();
        log.setIpAddress(ip);
        log.setEndpoint(endpoint);
        log.setAttackType(attackType);
        log.setTimestamp(LocalDateTime.now());
        threatRepository.save(log);
    }

    // get all attack logs
    public List<ThreatLog> getAllThreats() {
        return threatRepository.findAll();
    }

    // get single log by id
    public Optional<ThreatLog> getThreatById(Long id) {
        return threatRepository.findById(id);
    }

    // get logs by attack type
    public List<ThreatLog> getThreatsByType(
            String attackType) {
        return threatRepository
                .findByAttackType(attackType);
    }

    // get logs by IP address
    public List<ThreatLog> getThreatsByIp(
            String ipAddress) {
        return threatRepository
                .findByIpAddress(ipAddress);
    }

    // get latest 10 attacks
    public List<ThreatLog> getLatestThreats() {
        return threatRepository.findLatestThreats();
    }

    // delete log by id
    public boolean deleteThreat(Long id) {
        if (threatRepository.existsById(id)) {
            threatRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // get attack statistics
    public Map<String, Object> getStats() {

        Map<String, Object> stats = new HashMap<>();

        // total attack count
        Long total = threatRepository
                .countTotalThreats();
        stats.put("totalAttacks", total);

        // count by attack type
        List<Object[]> counts = threatRepository
                .countByAttackType();

        Map<String, Long> typeCounts = new HashMap<>();
        for (Object[] row : counts) {
            String type = (String) row[0];
            Long count = (Long) row[1];
            typeCounts.put(type, count);
        }
        stats.put("attacksByType", typeCounts);

        // latest 10 attacks
        stats.put("latestAttacks",
                threatRepository.findLatestThreats());

        return stats;
    }

}