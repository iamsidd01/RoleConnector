package com.example.agrisupply.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BidDto {

    @NotNull(message = "Bid amount cannot be null")
    @Positive(message = "Bid amount must be positive")
    private Double bidAmount;

    // saleItemId is usually passed as a path variable, not in the body
    // buyerId is determined from the authenticated user
}
