package com.example.agrisupply.security;

import com.example.agrisupply.model.Role;
import com.example.agrisupply.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Enable @PreAuthorize, @PostAuthorize
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for stateless APIs
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless sessions
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/public/**").permitAll() // Example for other public resources
                         // Swagger UI (if used) - adjust path as needed
                         .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Buyer endpoints
                        .requestMatchers("/api/buyer/requests/**").hasRole(Role.BUYER.name())
                        .requestMatchers("/api/buyer/bids/**").hasRole(Role.BUYER.name())
                        .requestMatchers(HttpMethod.GET, "/api/buyer/live-bids").hasRole(Role.BUYER.name()) // Specific GET allowed

                        // Supplier endpoints
                        .requestMatchers("/api/supplier/sales/**").hasRole(Role.SUPPLIER.name())
                         .requestMatchers(HttpMethod.GET, "/api/supplier/requests").hasRole(Role.SUPPLIER.name()) // Allow suppliers to GET buyer requests
                         .requestMatchers(HttpMethod.GET, "/api/supplier/bids/**").hasRole(Role.SUPPLIER.name()) // Allow suppliers to GET bids on their items


                        // Notification endpoints (accessible by authenticated users)
                        .requestMatchers("/api/notifications/**").hasAnyRole(
                                Role.BUYER.name(),
                                Role.SUPPLIER.name(),
                                Role.TRANSPORTER.name(), // Add other roles if they use notifications
                                Role.DRIVER.name()
                         )

                        // Example: Admin endpoints (if applicable)
                        // .requestMatchers("/api/admin/**").hasRole(Role.ADMIN.name())

                        // All other requests require authentication
                        .anyRequest().authenticated()
                );

        // Add JWT filter before the standard UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
