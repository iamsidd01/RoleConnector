package com.example.agrisupply.controller;

import com.example.agrisupply.dto.SaleItemDto;
import com.example.agrisupply.model.Bid;
import com.example.agrisupply.model.ItemRequest;
import com.example.agrisupply.model.SaleItem;
import com.example.agrisupply.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supplier")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPPLIER')") // Class-level authorization
public class SupplierController {

    private final SupplierService supplierService;

    // --- Sale Items ---

    @PostMapping("/sales")
    public ResponseEntity<SaleItem> postSaleItem(@Valid @RequestBody SaleItemDto saleItemDto) {
        SaleItem createdItem = supplierService.postSaleItem(saleItemDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    @GetMapping("/sales")
    public ResponseEntity<List<SaleItem>> getMySaleItems() {
        List<SaleItem> items = supplierService.getMySaleItems();
        return ResponseEntity.ok(items);
    }

    // --- Viewing Buyer Requests ---

    @GetMapping("/requests")
    public ResponseEntity<List<ItemRequest>> viewOpenBuyerRequests() {
        List<ItemRequest> openRequests = supplierService.viewOpenBuyerRequests();
        return ResponseEntity.ok(openRequests);
    }


    // --- Viewing Bids on Own Items ---

    @GetMapping("/bids/{saleItemId}")
    public ResponseEntity<List<Bid>> viewBidsOnMyItem(@PathVariable String saleItemId) {
         // Global exception handler will catch NotFoundException / ForbiddenAccessException
        List<Bid> bids = supplierService.viewBidsOnMyItem(saleItemId);
        return ResponseEntity.ok(bids);
    }


    // Add endpoints for cancelling sales, accepting bids, etc.
}
