package com.securityagent.api_security_agent.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;

@Data
@Entity
@Table
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String Username;

    private String Password;

    private String Role;
}
