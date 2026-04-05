package com.securityagent.api_security_agent.config;

import com.securityagent.api_security_agent.middleware.RateLimitFilter;
import com.securityagent.api_security_agent.middleware.SecurityFilter;
import com.securityagent.api_security_agent.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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
    private JwtAuthenticationFilter
            jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http) throws Exception {
        http
                // disable csrf
                .csrf(csrf -> csrf.disable())

                // disable session
                // we use JWT not sessions
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS)
                )

                // allow all requests
                // our JWT filter handles auth
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )

                // disable default login page
                .formLogin(form -> form.disable())

                // disable basic auth popup
                .httpBasic(basic -> basic.disable())

                // add our custom filters
                .addFilterBefore(rateLimitFilter,
                        UsernamePasswordAuthenticationFilter
                                .class)
                .addFilterBefore(securityFilter,
                        UsernamePasswordAuthenticationFilter
                                .class)
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter
                                .class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}