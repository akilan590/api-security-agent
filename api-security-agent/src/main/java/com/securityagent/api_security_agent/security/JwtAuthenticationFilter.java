package com.securityagent.api_security_agent.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ApiKeyService apiKeyService;

    // these paths are allowed without
    // API key or JWT token
    private static final List<String> PUBLIC_PATHS
            = List.of(
            "/auth/login",
            "/auth/register"
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Step 1 - check if path is public
        // if yes skip all checks
        boolean isPublic = PUBLIC_PATHS.stream()
                .anyMatch(path::startsWith);

        if (isPublic) {
            System.out.println(
                    "Public path, skipping auth: "
                            + path);
            filterChain.doFilter(request, response);
            return;
        }

        // Step 2 - check API key
        String apiKey = request
                .getHeader("X-API-KEY");

        if (!apiKeyService.isValid(apiKey)) {
            response.setStatus(401);
            response.setContentType(
                    "application/json");
            response.getWriter().write(
                    "{\"status\": 401," +
                            "\"message\": " +
                            "\"Invalid or missing API Key\"}"
            );
            System.out.println(
                    "BLOCKED: Invalid API key"
                            + " for path: " + path);
            return;
        }

        // Step 3 - check JWT token
        String authHeader = request
                .getHeader("Authorization");

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {
            response.setStatus(401);
            response.setContentType(
                    "application/json");
            response.getWriter().write(
                    "{\"status\": 401," +
                            "\"message\": " +
                            "\"Missing JWT token\"}"
            );
            System.out.println(
                    "BLOCKED: Missing JWT token");
            return;
        }

        // Step 4 - extract and validate token
        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            response.setStatus(401);
            response.setContentType(
                    "application/json");
            response.getWriter().write(
                    "{\"status\": 401," +
                            "\"message\": " +
                            "\"Invalid or expired JWT token\"}"
            );
            System.out.println(
                    "BLOCKED: Invalid JWT token");
            return;
        }

        // Step 5 - all checks passed
        String username =
                jwtUtil.extractUsername(token);
        System.out.println(
                "Authenticated: " + username);

        filterChain.doFilter(request, response);
    }

}