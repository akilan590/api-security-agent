package com.securityagent.api_security_agent.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ai_analysis_log")
public class AiAnalysisLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // input that was analyzed
    @Column(length = 1000)
    private String input;

    // what rules decided
    @Column(name = "rule_result")
    private String ruleResult;

    // what AI decided
    @Column(name = "ai_result")
    private String aiResult;

    // final decision after both checks
    @Column(name = "final_decision")
    private String finalDecision;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

}