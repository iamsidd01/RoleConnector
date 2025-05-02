package com.example.agrisupply.controller;

import com.example.agrisupply.dto.BidDto;
import com.example.agrisupply.dto.ItemRequestDto;
import com.example.agrisupply.model.Bid;
import com.example.agrisupply.model.ItemRequest;
import com.example.agrisupply.model.SaleItem;
import com.example.agrisupply.service.BuyerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buyer")
@RequiredArgsConstructor
@PreAuthorize("hasRole('BUYER')") // Class-level authorization
public class BuyerController {

    private final BuyerService buyerService;

    // --- Item Requests ---

    @PostMapping("/requests")
    public ResponseEntity<ItemRequest> postItemRequest(@Valid @RequestBody ItemRequestDto requestDto) {
        ItemRequest createdRequest = buyerService.postItemRequest(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRequest);
    }

    @GetMapping("/requests")
    public ResponseEntity<List<ItemRequest>> getMyItemRequests() {
        List<ItemRequest> requests = buyerService.getMyItemRequests();
        return ResponseEntity.ok(requests);
    }

    // --- Bidding ---

    @GetMapping("/live-bids")
    public ResponseEntity<List<SaleItem>> getLiveBiddingItems() {
        List<SaleItem> liveItems = buyerService.viewLiveBiddingItems();
        return ResponseEntity.ok(liveItems);
    }

    @PostMapping("/bids/{saleItemId}")
    public ResponseEntity<Bid> placeBid(@PathVariable String saleItemId, @Valid @RequestBody BidDto bidDto) {
        // Exception handling for NotFound, BadRequest, Forbidden is typically done
        // via a global @ControllerAdvice handler, but you could add try-catch here too.
        Bid placedBid = buyerService.placeBid(saleItemId, bidDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(placedBid);
    }

    @GetMapping("/bids")
    public ResponseEntity<List<Bid>> getMyBids() {
        List<Bid> myBids = buyerService.getMyBids();
        return ResponseEntity.ok(myBids);
    }

    // Add endpoints for cancelling requests, etc.
}
