package com.securityagent.api_security_agent.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ApiKeyService apiKeyService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // allow login endpoint without token
        // otherwise nobody can login
        if (path.equals("/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 1 - check API key
        String apiKey = request.getHeader("X-API-KEY");

        if (!apiKeyService.isValid(apiKey)) {
            response.setStatus(401);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"status\": 401," +
                            "\"message\": \"Invalid or missing API Key\"}"
            );
            System.out.println("BLOCKED: Invalid API key");
            return;
        }

        // Step 2 - check JWT token
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {
            response.setStatus(401);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"status\": 401," +
                            "\"message\": \"Missing JWT token\"}"
            );
            System.out.println("BLOCKED: Missing JWT token");
            return;
        }

        // extract token from header
        // header format: "Bearer eyJhbGci..."
        String token = authHeader.substring(7);

        // Step 3 - validate token
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(401);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"status\": 401," +
                            "\"message\": \"Invalid or expired JWT token\"}"
            );
            System.out.println("BLOCKED: Invalid JWT token");
            return;
        }

        // Step 4 - token is valid, allow request
        String username = jwtUtil.extractUsername(token);
        System.out.println("Authenticated user: " + username);

        filterChain.doFilter(request, response);
    }

}