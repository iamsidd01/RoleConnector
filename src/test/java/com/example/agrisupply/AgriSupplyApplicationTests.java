package com.example.agrisupply;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Basic integration test to ensure the Spring application context loads successfully.
 */
@SpringBootTest // Loads the full application context
@ActiveProfiles("test") // Activate a specific profile for tests (loads application-test.properties)
class AgriSupplyApplicationTests {

    /**
     * Test that the Spring application context loads without throwing exceptions.
     * This verifies basic configuration, component scanning, and dependency injection.
     */
    @Test
    void contextLoads() {
        // If this test passes, the Spring context loaded successfully.
        // Add a simple assertion to make the test purpose clearer.
        assertTrue(true, "Spring application context should load without errors.");
        System.out.println("Spring context loaded successfully for tests using 'test' profile.");
    }

}