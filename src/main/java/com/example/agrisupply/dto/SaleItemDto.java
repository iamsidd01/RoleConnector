package com.example.agrisupply.dto;

import com.example.agrisupply.model.SaleItem;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SaleItemDto {

    @NotEmpty(message = "Item name cannot be empty")
    private String itemName;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Double quantity;

    @NotEmpty(message = "Unit cannot be empty")
    private String unit;

    private String description;

    @NotNull(message = "Post type (DIRECT or BIDDING) is required")
    private SaleItem.PostType postType;

    // Fields specific to BIDDING post type
    private SaleItem.BiddingType biddingType; // Required if postType is BIDDING
    private LocalDateTime biddingStartTime; // Required for SLOT bidding
    private Double startingBidPrice; // Optional starting price for bidding
    // biddingEndTime is calculated based on startTime/now and duration
}
