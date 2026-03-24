package com.securityagent.api_security_agent.repository;



import com.securityagent.api_security_agent.model.ThreatLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThreatRepository extends JpaRepository<ThreatLog, Long> {

}

