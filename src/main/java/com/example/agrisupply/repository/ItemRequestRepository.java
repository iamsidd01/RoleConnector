package com.example.agrisupply.repository;

import com.example.agrisupply.model.ItemRequest;
import com.example.agrisupply.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRequestRepository extends MongoRepository<ItemRequest, String> {
    List<ItemRequest> findByBuyer(User buyer);
    List<ItemRequest> findByStatus(ItemRequest.RequestStatus status);
    // Add custom queries if needed
}
