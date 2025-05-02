package com.example.agrisupply.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "sale_items")
@Data
@NoArgsConstructor
public class SaleItem {

    @Id
    private String id;

    @DBRef // Reference to the supplier who posted the item
    private User supplier; // Ensure this user has the SUPPLIER role

    private String itemName;
    private Double quantity;
    private String unit;
    private String description;
    private PostType postType; // DIRECT or BIDDING
    private BiddingType biddingType; // INSTANT or SLOT (only if postType is BIDDING)
    private LocalDateTime biddingStartTime; // Relevant for BIDDING type
    private LocalDateTime biddingEndTime; // Relevant for BIDDING type (usually startTime + duration)
    private BidStatus bidStatus = BidStatus.PENDING; // Default status for bidding items
    private LocalDateTime createdAt;
    private Double startingBidPrice; // Optional: For bidding items

    public SaleItem(User supplier, String itemName, Double quantity, String unit, String description, PostType postType) {
        this.supplier = supplier;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unit = unit;
        this.description = description;
        this.postType = postType;
        this.createdAt = LocalDateTime.now();
    }

    public enum PostType {
        DIRECT, // Direct sale
        BIDDING // Item is up for bidding
    }

    public enum BiddingType {
        INSTANT, // Bidding starts immediately
        SLOT     // Bidding starts at a scheduled time slot
    }

    public enum BidStatus {
        PENDING,  // Bidding slot not yet started
        LIVE,     // Bidding is currently active
        CLOSED,   // Bidding has ended
        SOLD,     // Item sold (either direct or highest bid accepted)
        CANCELLED // Sale/Bidding cancelled
    }
}
