package com.securityagent.api_security_agent.repository;
import com.securityagent.api_security_agent.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

    @Repository
    public interface UserRepository extends JpaRepository<User, Long> {

        // finds user by username
        // used during login
        Optional<User> findByUsername(String username);

    }

