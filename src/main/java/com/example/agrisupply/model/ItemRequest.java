package com.example.agrisupply.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "item_requests")
@Data
@NoArgsConstructor
public class ItemRequest {

    @Id
    private String id;

    @DBRef // Reference to the buyer who posted the request
    private User buyer; // Ensure this user has the BUYER role

    private String itemName;
    private Double quantity; // Using Double for flexibility (e.g., weight, volume)
    private String unit; // e.g., kg, tonnes, liters, pieces
    private String description;
    private LocalDateTime createdAt;
    private RequestStatus status = RequestStatus.OPEN; // Default status

    public ItemRequest(User buyer, String itemName, Double quantity, String unit, String description) {
        this.buyer = buyer;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unit = unit;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    public enum RequestStatus {
        OPEN, FULFILLED, CANCELLED
    }
}
