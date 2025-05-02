package com.example.agrisupply.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * REST Controller for endpoints that are publicly accessible without authentication.
 */
@RestController
@RequestMapping("/public") // Base path for all endpoints in this controller
@Tag(name = "Public Access", description = "Endpoints accessible without authentication") // Swagger Tag
public class PublicController {

    /**
     * Provides a simple health check endpoint to verify the application is running.
     * @return A map containing the application status.
     */
    @Operation(summary = "Health Check", description = "Returns the operational status of the application.")
    @ApiResponse(responseCode = "200", description = "Application is running",
                 content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                 schema = @Schema(type = "object", example = "{\"status\": \"UP\"}")))
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        // Simple health check endpoint accessible without authentication
        Map<String, String> response = Map.of("status", "UP");
        return ResponseEntity.ok(response);
    }

    /**
     * Provides the current server time (as an example of public info).
     * @return A map containing the current server time as an ISO-8601 timestamp.
     */
    @Operation(summary = "Server Time", description = "Returns the current server timestamp (UTC).")
    @ApiResponse(responseCode = "200", description = "Current server time",
                 content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                 schema = @Schema(type = "object", example = "{\"serverTime\": \"2023-10-27T10:30:00Z\"}")))
    @GetMapping("/time")
    public ResponseEntity<Map<String, Instant>> getCurrentTime() {
        Map<String, Instant> response = Map.of("serverTime", Instant.now());
        return ResponseEntity.ok(response);
    }

    // Add other public information endpoints here if needed (e.g., API version, general info)
}