package com.example.agrisupply.service;

import com.example.agrisupply.dto.SaleItemDto;
import com.example.agrisupply.exception.BadRequestException;
import com.example.agrisupply.exception.ForbiddenAccessException;
import com.example.agrisupply.exception.NotFoundException;
import com.example.agrisupply.model.*;
import com.example.agrisupply.repository.BidRepository;
import com.example.agrisupply.repository.ItemRequestRepository;
import com.example.agrisupply.repository.SaleItemRepository;
import com.example.agrisupply.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SaleItemRepository saleItemRepository;
    private final ItemRequestRepository itemRequestRepository; // To view buyer requests
    private final BidRepository bidRepository; // To view bids on own items
    private final AuthService authService;
    private final NotificationService notificationService; // Optional
    private final UserRepository userRepository; // To find buyers for notifications

    private static final long BIDDING_DURATION_HOURS = 1; // Default duration for instant bids

    /**
     * Creates a new sale item listing for the currently authenticated supplier.
     * Handles both DIRECT and BIDDING types.
     *
     * @param saleItemDto DTO containing the sale item details.
     * @return The created SaleItem entity.
     * @throws ForbiddenAccessException if the user is not a supplier.
     * @throws BadRequestException      if bidding details are invalid.
     */
    public SaleItem postSaleItem(SaleItemDto saleItemDto) {
        User currentUser = authService.getCurrentUserOrFail();
        if (!currentUser.getRoles().contains(Role.SUPPLIER)) {
            throw new ForbiddenAccessException("Only suppliers can post sale items.");
        }

        SaleItem newItem = new SaleItem(
                currentUser,
                saleItemDto.getItemName(),
                saleItemDto.getQuantity(),
                saleItemDto.getUnit(),
                saleItemDto.getDescription(),
                saleItemDto.getPostType()
        );

        if (saleItemDto.getPostType() == SaleItem.PostType.BIDDING) {
            newItem.setBiddingType(saleItemDto.getBiddingType());
            newItem.setStartingBidPrice(saleItemDto.getStartingBidPrice()); // Can be null

            if (saleItemDto.getBiddingType() == null) {
                throw new BadRequestException("Bidding type (INSTANT or SLOT) is required for BIDDING posts.");
            }

            LocalDateTime now = LocalDateTime.now();
            if (saleItemDto.getBiddingType() == SaleItem.BiddingType.INSTANT) {
                newItem.setBiddingStartTime(now);
                newItem.setBiddingEndTime(now.plusHours(BIDDING_DURATION_HOURS));
                newItem.setBidStatus(SaleItem.BidStatus.LIVE); // Instant bidding starts immediately
            } else { // SLOT bidding
                if (saleItemDto.getBiddingStartTime() == null) {
                    throw new BadRequestException("Bidding start time is required for SLOT bidding.");
                }
                if (saleItemDto.getBiddingStartTime().isBefore(now)) {
                     throw new BadRequestException("Slot bidding start time cannot be in the past.");
                }
                 // Ensure slot is reasonable (e.g., not too far in future? - add check if needed)

                newItem.setBiddingStartTime(saleItemDto.getBiddingStartTime());
                 // Set end time based on start time + fixed duration (e.g., 1 hour)
                newItem.setBiddingEndTime(saleItemDto.getBiddingStartTime().plusHours(BIDDING_DURATION_HOURS));
                newItem.setBidStatus(SaleItem.BidStatus.PENDING); // Slot bidding is pending until start time
            }
             // Validate end time is after start time (should be guaranteed by logic above, but good practice)
             if (!newItem.getBiddingEndTime().isAfter(newItem.getBiddingStartTime())) {
                 throw new BadRequestException("Bidding end time must be after start time.");
             }

        } else { // DIRECT sale
            newItem.setBidStatus(SaleItem.BidStatus.SOLD); // Or maybe an 'AVAILABLE' status? Using SOLD for simplicity.
        }

        SaleItem savedItem = saleItemRepository.save(newItem);

        // If it's a bidding item (instant or slot), notify buyers
        if (savedItem.getPostType() == SaleItem.PostType.BIDDING) {
            notifyBuyersOfNewBidding(savedItem);
        }

        return savedItem;
    }

    /**
     * Retrieves all sale items posted by the currently authenticated supplier.
     *
     * @return A list of SaleItem entities.
     */
    public List<SaleItem> getMySaleItems() {
        User currentUser = authService.getCurrentUserOrFail();
        return saleItemRepository.findBySupplier(currentUser);
    }

    /**
     * Allows suppliers to view open purchase requests from buyers.
     *
     * @return A list of ItemRequest entities with OPEN status.
     */
    public List<ItemRequest> viewOpenBuyerRequests() {
        // Ensure user is a supplier
        User currentUser = authService.getCurrentUserOrFail();
        if (!currentUser.getRoles().contains(Role.SUPPLIER)) {
             throw new ForbiddenAccessException("Only suppliers can view buyer requests.");
        }
        return itemRequestRepository.findByStatus(ItemRequest.RequestStatus.OPEN);
    }


    /**
     * Retrieves all bids placed on a specific sale item owned by the current supplier.
     *
     * @param saleItemId The ID of the SaleItem.
     * @return A list of Bid entities.
     * @throws NotFoundException        if the SaleItem doesn't exist.
     * @throws ForbiddenAccessException if the current user doesn't own the SaleItem.
     */
    public List<Bid> viewBidsOnMyItem(String saleItemId) {
        User currentUser = authService.getCurrentUserOrFail();
        SaleItem saleItem = saleItemRepository.findById(saleItemId)
                .orElseThrow(() -> new NotFoundException("Sale item not found with ID: " + saleItemId));

        if (!saleItem.getSupplier().getId().equals(currentUser.getId())) {
            throw new ForbiddenAccessException("You can only view bids on your own sale items.");
        }

        // Return bids sorted by amount descending (highest first)
        return bidRepository.findBySaleItemOrderByBidAmountDesc(saleItem);
    }


     /**
      * Helper method to notify all buyers about a new bidding event.
      * @param saleItem The SaleItem that is up for bidding.
      */
    private void notifyBuyersOfNewBidding(SaleItem saleItem) {
        List<User> buyers = userRepository.findByRolesIn(Set.of(Role.BUYER));
        String message;
        if (saleItem.getBiddingType() == SaleItem.BiddingType.INSTANT) {
             message = String.format("New instant bidding started for %s! Ends at %s.",
                 saleItem.getItemName(), saleItem.getBiddingEndTime().truncatedTo(ChronoUnit.MINUTES));
        } else {
             message = String.format("New slot bidding scheduled for %s. Starts at %s, ends at %s.",
                 saleItem.getItemName(), saleItem.getBiddingStartTime().truncatedTo(ChronoUnit.MINUTES), saleItem.getBiddingEndTime().truncatedTo(ChronoUnit.MINUTES));
        }

        for (User buyer : buyers) {
            notificationService.createNotification(
                    buyer,
                    message,
                    Notification.NotificationType.NEW_BIDDING_EVENT,
                    saleItem.getId()
            );
        }
    }


    // Add methods for cancelling sales, accepting bids (more complex logic needed), etc.
}
