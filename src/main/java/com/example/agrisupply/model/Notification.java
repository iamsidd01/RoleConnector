package com.example.agrisupply.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "notifications")
@Data
@NoArgsConstructor
public class Notification {

    @Id
    private String id;

    @DBRef // User receiving the notification
    private User recipient;

    private String message;
    private NotificationType type;
    private String relatedItemId; // ID of the SaleItem, ItemRequest, Bid, etc.
    private LocalDateTime createdAt;
    private boolean isRead = false;

    public Notification(User recipient, String message, NotificationType type, String relatedItemId) {
        this.recipient = recipient;
        this.message = message;
        this.type = type;
        this.relatedItemId = relatedItemId;
        this.createdAt = LocalDateTime.now();
    }

    public enum NotificationType {
        NEW_BIDDING_EVENT, // For buyers when a supplier creates a bid
        BID_PLACED,        // For supplier when a bid is placed on their item
        BID_WON,           // For buyer when their bid wins
        BID_LOST,          // For buyer when they are outbid or lose
        ITEM_REQUEST_MATCH, // Optional: If matching logic is implemented
        SALE_CLOSED        // For buyers/supplier when a bidding event ends
        // Add more types as needed
    }
}
