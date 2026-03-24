package com.securityagent.api_security_agent.service;

import com.securityagent.api_security_agent.model.ThreatLog;
import com.securityagent.api_security_agent.repository.ThreatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ThreatService {

    @Autowired
    private ThreatRepository threatRepository;

    public void logThreat(String ip, String endpoint, String attackType) {
        ThreatLog log = new ThreatLog();
        log.setIpAddress(ip);
        log.setEndpoint(endpoint);
        log.setAttackType(attackType);
        log.setTimestamp(LocalDateTime.now());
        threatRepository.save(log);
    }

    public List<ThreatLog> getAllThreats() {
        return threatRepository.findAll();
    }

}