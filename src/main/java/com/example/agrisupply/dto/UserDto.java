package com.example.agrisupply.dto;

import com.example.agrisupply.model.Role;
import com.example.agrisupply.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO for representing User information in API responses.
 * Excludes sensitive data like passwords.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents basic user information")
public class UserDto {

    @Schema(description = "Unique identifier of the user", example = "60c72b2f9b1e8a1b1c8e4f5a")
    private String id;

    @Schema(description = "User's email address (also username)", example = "buyer@example.com")
    private String email;

    @Schema(description = "Set of roles assigned to the user", example = "[\"BUYER\"]")
    private Set<String> roles; // Return role names as strings

    // Add other non-sensitive fields if needed (e.g., fullName, registrationDate)

    /**
     * Factory method to convert a User entity to a UserDto.
     * @param user The User entity.
     * @return A UserDto object, or null if the input user is null.
     */
    public static UserDto fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(
                user.getId(),
                user.getEmail(),
                // Convert Role enums to their string names for the DTO
                user.getRoles().stream().map(Role::name).collect(Collectors.toSet())
                // Map other fields here if added
        );
    }
}