package com.securityagent.api_security_agent.repository;

import com.securityagent.api_security_agent.model.ThreatLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThreatRepository
        extends JpaRepository<ThreatLog, Long> {

    // find all logs by attack type
    // example: findByAttackType("SQL_INJECTION")
    List<ThreatLog> findByAttackType(String attackType);

    // find all logs by IP address
    List<ThreatLog> findByIpAddress(String ipAddress);

    // count how many times each attack type occurred
    // returns list of [attackType, count]
    @Query("SELECT t.attackType, COUNT(t) " +
            "FROM ThreatLog t " +
            "GROUP BY t.attackType")
    List<Object[]> countByAttackType();

    // get latest 10 attacks
    @Query("SELECT t FROM ThreatLog t " +
            "ORDER BY t.timestamp DESC")
    List<ThreatLog> findLatestThreats();

    // count total attacks
    @Query("SELECT COUNT(t) FROM ThreatLog t")
    Long countTotalThreats();

}