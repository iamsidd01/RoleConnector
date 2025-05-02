package com.example.agrisupply.repository;

import com.example.agrisupply.model.Bid;
import com.example.agrisupply.model.SaleItem;
import com.example.agrisupply.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends MongoRepository<Bid, String> {
    List<Bid> findBySaleItem(SaleItem saleItem);
    List<Bid> findByBuyer(User buyer);
    List<Bid> findBySaleItemOrderByBidAmountDesc(SaleItem saleItem);
    Optional<Bid> findTopBySaleItemOrderByBidAmountDesc(SaleItem saleItem);
    List<Bid> findBySaleItem(SaleItem saleItem, Sort sort); // For more complex sorting

     // Find the highest bid for a specific sale item
    default Optional<Bid> findHighestBidForSaleItem(SaleItem saleItem) {
        return findTopBySaleItemOrderByBidAmountDesc(saleItem);
    }
}
