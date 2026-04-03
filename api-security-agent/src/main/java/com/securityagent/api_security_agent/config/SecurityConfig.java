package com.securityagent.api_security_agent.config;

import com.securityagent.api_security_agent.middleware.RateLimitFilter;
import com.securityagent.api_security_agent.middleware.SecurityFilter;
import com.securityagent.api_security_agent.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Autowired
    private RateLimitFilter rateLimitFilter;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/login",
                                "/auth/register").permitAll()
                        .anyRequest().authenticated()
                )
                // runs in this exact order:

                // 1. RateLimitFilter runs first
                // blocks IPs sending too many requests
                .addFilterBefore(rateLimitFilter,
                        UsernamePasswordAuthenticationFilter.class)

                // 2. SecurityFilter runs second
                // blocks SQL injection and XSS attacks
                .addFilterBefore(securityFilter,
                        UsernamePasswordAuthenticationFilter.class)

                // 3. JwtAuthenticationFilter runs third
                // checks API key and JWT token
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}