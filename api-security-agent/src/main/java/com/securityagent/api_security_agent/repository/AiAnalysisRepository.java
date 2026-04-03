package com.securityagent.api_security_agent.repository;

import com.securityagent.api_security_agent.model.AiAnalysisLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiAnalysisRepository
        extends JpaRepository<AiAnalysisLog, Long> {

    // find logs where AI caught something
    // that rules missed
    List<AiAnalysisLog> findByAiResult(
            String aiResult);

    // find all logs where final decision
    // was not safe
    List<AiAnalysisLog> findByFinalDecision(
            String finalDecision);

    // get latest AI analysis logs
    @Query("SELECT a FROM AiAnalysisLog a " +
            "ORDER BY a.timestamp DESC")
    List<AiAnalysisLog> findLatestAnalysis();

}