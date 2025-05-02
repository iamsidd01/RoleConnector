package com.example.agrisupply.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.error("Responding with unauthorized error. Message - {}", authException.getMessage());
        // Send 401 Unauthorized response
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        // You could customize the response body here if needed (e.g., JSON error message)
        // response.setContentType("application/json");
        // response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + authException.getMessage() + "\"}");
    }
}
