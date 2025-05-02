package com.example.agrisupply.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemRequestDto {

    @NotEmpty(message = "Item name cannot be empty")
    private String itemName;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 0, message = "Quantity cannot be negative") // Allow 0 if needed? Or use @Positive?
    private Double quantity;

    @NotEmpty(message = "Unit cannot be empty")
    private String unit; // e.g., kg, tonnes, liters, pieces

    private String description; // Optional or require minimum length?
}
