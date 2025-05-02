package com.example.agrisupply.service;

import com.example.agrisupply.dto.BidDto;
import com.example.agrisupply.dto.ItemRequestDto;
import com.example.agrisupply.exception.BadRequestException;
import com.example.agrisupply.exception.ForbiddenAccessException;
import com.example.agrisupply.exception.NotFoundException;
import com.example.agrisupply.model.*;
import com.example.agrisupply.repository.BidRepository;
import com.example.agrisupply.repository.ItemRequestRepository;
import com.example.agrisupply.repository.SaleItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BuyerService {

    private final ItemRequestRepository itemRequestRepository;
    private final SaleItemRepository saleItemRepository;
    private final BidRepository bidRepository;
    private final AuthService authService;
    private final NotificationService notificationService; // Optional

    /**
     * Creates a new purchase request for the currently authenticated buyer.
     *
     * @param requestDto DTO containing the item request details.
     * @return The created ItemRequest entity.
     */
    public ItemRequest postItemRequest(ItemRequestDto requestDto) {
        User currentUser = authService.getCurrentUserOrFail();

        // Optional: Verify user has BUYER role if not handled by security context
        if (!currentUser.getRoles().contains(Role.BUYER)) {
            throw new ForbiddenAccessException("Only buyers can post item requests.");
        }

        ItemRequest newItemRequest = new ItemRequest(
                currentUser,
                requestDto.getItemName(),
                requestDto.getQuantity(),
                requestDto.getUnit(),
                requestDto.getDescription()
        );
        return itemRequestRepository.save(newItemRequest);
    }

    /**
     * Retrieves all item requests posted by the currently authenticated buyer.
     *
     * @return A list of ItemRequest entities.
     */
    public List<ItemRequest> getMyItemRequests() {
        User currentUser = authService.getCurrentUserOrFail();
        return itemRequestRepository.findByBuyer(currentUser);
    }

    /**
     * Finds SaleItems that are currently available for bidding (status is LIVE).
     *
     * @return A list of live SaleItem entities.
     */
    public List<SaleItem> viewLiveBiddingItems() {
        // Ensure only buyers can view these? Or allow anyone? Assuming buyers for now.
        authService.getCurrentUserOrFail(); // Just check if authenticated
        // Could filter further based on buyer preferences if needed
        return saleItemRepository.findLiveBiddingItemsByStatus();
    }

    /**
     * Places a bid on a specific sale item.
     *
     * @param saleItemId The ID of the SaleItem to bid on.
     * @param bidDto     DTO containing the bid amount.
     * @return The created Bid entity.
     * @throws NotFoundException     if the SaleItem doesn't exist.
     * @throws BadRequestException   if the bidding is not live or the bid amount is invalid.
     * @throws ForbiddenAccessException if the current user is not a buyer.
     */
    public Bid placeBid(String saleItemId, BidDto bidDto) {
        User currentUser = authService.getCurrentUserOrFail();
        if (!currentUser.getRoles().contains(Role.BUYER)) {
            throw new ForbiddenAccessException("Only buyers can place bids.");
        }

        SaleItem saleItem = saleItemRepository.findById(saleItemId)
                .orElseThrow(() -> new NotFoundException("Sale item not found with ID: " + saleItemId));

        // Check if bidding is actually live for this item
        if (saleItem.getPostType() != SaleItem.PostType.BIDDING || saleItem.getBidStatus() != SaleItem.BidStatus.LIVE) {
            throw new BadRequestException("Bidding for this item is not currently live.");
        }

         // Optional: Check against explicit bidding start/end times again for robustness
         LocalDateTime now = LocalDateTime.now();
         if (now.isBefore(saleItem.getBiddingStartTime()) || now.isAfter(saleItem.getBiddingEndTime())) {
             // This might indicate a status update issue, log a warning
             System.err.println("Warning: Bid attempted on item " + saleItemId + " outside explicit time window, despite LIVE status.");
             throw new BadRequestException("Bidding time window mismatch. Bidding might have just closed.");
         }

        // Check if the new bid is higher than the current highest bid
        Optional<Bid> highestBidOpt = bidRepository.findHighestBidForSaleItem(saleItem);
        if (highestBidOpt.isPresent() && bidDto.getBidAmount() <= highestBidOpt.get().getBidAmount()) {
            throw new BadRequestException("Your bid must be higher than the current highest bid of " + highestBidOpt.get().getBidAmount());
        }

        // Also check against starting price if applicable and no bids yet
        if (highestBidOpt.isEmpty() && saleItem.getStartingBidPrice() != null && bidDto.getBidAmount() < saleItem.getStartingBidPrice()) {
             throw new BadRequestException("Your bid must be equal to or higher than the starting price of " + saleItem.getStartingBidPrice());
        }

        Bid newBid = new Bid(saleItem, currentUser, bidDto.getBidAmount());
        Bid savedBid = bidRepository.save(newBid);

        // Optional: Send notification to the supplier
        notificationService.createNotification(
            saleItem.getSupplier(),
            "A new bid of " + savedBid.getBidAmount() + " was placed on your item: " + saleItem.getItemName(),
            Notification.NotificationType.BID_PLACED,
            saleItem.getId()
        );

        return savedBid;
    }

    /**
     * Retrieves all bids placed by the currently authenticated buyer.
     *
     * @return A list of Bid entities.
     */
    public List<Bid> getMyBids() {
        User currentUser = authService.getCurrentUserOrFail();
        return bidRepository.findByBuyer(currentUser);
    }

    // Add methods for cancelling requests, viewing bid history for a specific item, etc.
}
