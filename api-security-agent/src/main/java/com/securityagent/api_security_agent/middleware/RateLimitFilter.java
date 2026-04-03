package com.securityagent.api_security_agent.middleware;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // stores how many requests each IP has sent
    // ConcurrentHashMap is thread safe
    // multiple requests at same time won't cause issues
    private final Map<String, AtomicInteger> requestCounts
            = new ConcurrentHashMap<>();

    // stores when each IP started counting
    private final Map<String, LocalDateTime> windowStart
            = new ConcurrentHashMap<>();

    // max requests allowed per IP
    private static final int MAX_REQUESTS = 100;

    // time window in minutes
    // counts reset after this time
    private static final int WINDOW_MINUTES = 1;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // Step 1 - get IP address of sender
        String ip = request.getRemoteAddr();

        // Step 2 - if first request from this IP
        // start counting from now
        requestCounts.putIfAbsent(ip,
                new AtomicInteger(0));
        windowStart.putIfAbsent(ip,
                LocalDateTime.now());

        // Step 3 - check if time window has passed
        // if yes, reset the counter for this IP
        LocalDateTime start = windowStart.get(ip);
        LocalDateTime now = LocalDateTime.now();

        boolean windowExpired = now.isAfter(
                start.plusMinutes(WINDOW_MINUTES));

        if (windowExpired) {
            // reset counter
            requestCounts.get(ip).set(0);
            // reset window start time
            windowStart.put(ip, now);
            System.out.println(
                    "Rate limit counter reset for IP: " + ip);
        }

        // Step 4 - count this request
        int count = requestCounts.get(ip)
                .incrementAndGet();

        System.out.println(
                "Request count for IP " + ip
                        + ": " + count + "/" + MAX_REQUESTS);

        // Step 5 - block if too many requests
        if (count > MAX_REQUESTS) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{" +
                            "\"status\": 429," +
                            "\"message\": \"Too many requests." +
                            " Try again after 1 minute.\"," +
                            "\"ip\": \"" + ip + "\"," +
                            "\"requestCount\": " + count +
                            "}"
            );

            System.out.println(
                    "RATE LIMIT EXCEEDED for IP: " + ip
                            + " count: " + count);
            return;
        }

        // Step 6 - within limit, allow request
        filterChain.doFilter(request, response);
    }

}