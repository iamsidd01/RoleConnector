package com.example.agrisupply.repository;

import com.example.agrisupply.model.SaleItem;
import com.example.agrisupply.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleItemRepository extends MongoRepository<SaleItem, String> {
    List<SaleItem> findBySupplier(User supplier);
    List<SaleItem> findByPostType(SaleItem.PostType postType);
    List<SaleItem> findByBidStatus(SaleItem.BidStatus bidStatus);
    List<SaleItem> findByPostTypeAndBidStatus(SaleItem.PostType postType, SaleItem.BidStatus bidStatus);
    List<SaleItem> findByPostTypeAndBiddingStartTimeBeforeAndBiddingEndTimeAfter(SaleItem.PostType postType, LocalDateTime now1, LocalDateTime now2);
    List<SaleItem> findByPostTypeAndBiddingEndTimeBeforeAndBidStatus(SaleItem.PostType postType, LocalDateTime now, SaleItem.BidStatus status);
    List<SaleItem> findByPostTypeAndBiddingStartTimeAfterAndBidStatus(SaleItem.PostType postType, LocalDateTime now, SaleItem.BidStatus status);


    // Find live bidding items
    default List<SaleItem> findLiveBiddingItems() {
        LocalDateTime now = LocalDateTime.now();
        return findByPostTypeAndBiddingStartTimeBeforeAndBiddingEndTimeAfter(SaleItem.PostType.BIDDING, now, now);
    }

     // Find bidding items that are live based on status
    default List<SaleItem> findLiveBiddingItemsByStatus() {
        return findByPostTypeAndBidStatus(SaleItem.PostType.BIDDING, SaleItem.BidStatus.LIVE);
    }
}
