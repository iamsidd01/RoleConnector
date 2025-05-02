package com.example.agrisupply.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "bids")
@Data
@NoArgsConstructor
public class Bid {

    @Id
    private String id;

    @DBRef // Reference to the sale item being bid on
    private SaleItem saleItem;

    @DBRef // Reference to the buyer placing the bid
    private User buyer; // Ensure this user has the BUYER role

    private Double bidAmount;
    private LocalDateTime bidTime;

    public Bid(SaleItem saleItem, User buyer, Double bidAmount) {
        this.saleItem = saleItem;
        this.buyer = buyer;
        this.bidAmount = bidAmount;
        this.bidTime = LocalDateTime.now();
    }
}
