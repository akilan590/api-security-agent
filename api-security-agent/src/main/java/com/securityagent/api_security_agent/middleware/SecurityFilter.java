package com.securityagent.api_security_agent.middleware;

import com.securityagent.api_security_agent.agent.ThreatDetector;
import com.securityagent.api_security_agent.service.ThreatService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private ThreatDetector threatDetector;

    @Autowired
    private ThreatService threatService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String queryString = request.getQueryString();
        String ipAddress = request.getRemoteAddr();
        String endpoint = request.getRequestURI();

        if (queryString != null) {

            String decodedQuery = URLDecoder.decode(queryString, StandardCharsets.UTF_8);

            System.out.println("Incoming query: " + decodedQuery);
            System.out.println("Endpoint: " + endpoint);

            String threat = threatDetector.detect(decodedQuery);

            System.out.println("Threat detected: " + threat);

            if (!threat.equalsIgnoreCase("SAFE")) {

                threatService.logThreat(ipAddress, endpoint, threat);

                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"error\": \"" + threat + " detected\", " +
                                "\"status\": 403, " +
                                "\"ip\": \"" + ipAddress + "\"}"
                );

                return;
            }
        }

        filterChain.doFilter(request, response);
    }

}