package com.example.agrisupply.service;

import com.example.agrisupply.model.Bid;
import com.example.agrisupply.model.Notification;
import com.example.agrisupply.model.SaleItem;
import com.example.agrisupply.repository.BidRepository;
import com.example.agrisupply.repository.SaleItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import if using transactions

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j // Use Lombok's logger
public class BiddingService {

    private final SaleItemRepository saleItemRepository;
    private final BidRepository bidRepository;
    private final NotificationService notificationService;

    // Run every minute to check for bidding status updates
    // Consider adjusting the rate based on expected load and precision requirements
    @Scheduled(fixedRate = 60000) // 60 * 1000 milliseconds = 1 minute
    @Transactional // Optional: Wrap in transaction if multiple updates need atomicity
    public void updateBiddingStatus() {
        log.info("Running scheduled task: updateBiddingStatus at {}", LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now();

        // 1. Find PENDING bids that should now be LIVE
        List<SaleItem> itemsToStart = saleItemRepository.findByPostTypeAndBiddingStartTimeAfterAndBidStatus(
                SaleItem.PostType.BIDDING, now, SaleItem.BidStatus.PENDING
        );

         // Corrected Query: Find PENDING bids where start time is now or in the past
        itemsToStart = saleItemRepository.findByPostTypeAndBidStatus(SaleItem.PostType.BIDDING, SaleItem.BidStatus.PENDING)
                .stream()
                .filter(item -> !item.getBiddingStartTime().isAfter(now)) // Start time is now or past
                .toList();


        for (SaleItem item : itemsToStart) {
            // Double check if it hasn't ended already (edge case if scheduler is slow)
            if (now.isBefore(item.getBiddingEndTime())) {
                 log.info("Starting bidding for item: {} ({})", item.getItemName(), item.getId());
                item.setBidStatus(SaleItem.BidStatus.LIVE);
                saleItemRepository.save(item);
                // Optionally notify supplier/buyers that bidding has started
                 // notificationService.createNotification(...)
            } else {
                 // Start time is past, but end time is also past - should have been closed
                 log.warn("Item {} should have started but already ended. Setting to CLOSED.", item.getId());
                 item.setBidStatus(SaleItem.BidStatus.CLOSED);
                 saleItemRepository.save(item);
                 // Consider processing results immediately here too
                 processBiddingResults(item);
            }
        }

        // 2. Find LIVE bids that should now be CLOSED
        List<SaleItem> itemsToEnd = saleItemRepository.findByPostTypeAndBiddingEndTimeBeforeAndBidStatus(
                SaleItem.PostType.BIDDING, now, SaleItem.BidStatus.LIVE
        );

        for (SaleItem item : itemsToEnd) {
            log.info("Closing bidding for item: {} ({})", item.getItemName(), item.getId());
            item.setBidStatus(SaleItem.BidStatus.CLOSED);
            saleItemRepository.save(item);
            // Process results (find winner, notify participants)
            processBiddingResults(item);
        }

        log.info("Finished scheduled task: updateBiddingStatus");
    }

    /**
     * Processes the results of a bidding event once it closes.
     * Finds the highest bidder and notifies relevant parties.
     *
     * @param closedSaleItem The SaleItem whose bidding has just closed.
     */
    private void processBiddingResults(SaleItem closedSaleItem) {
        log.info("Processing bidding results for closed item: {} ({})", closedSaleItem.getItemName(), closedSaleItem.getId());

        Optional<Bid> highestBidOpt = bidRepository.findHighestBidForSaleItem(closedSaleItem);

        if (highestBidOpt.isPresent()) {
            Bid winningBid = highestBidOpt.get();
            log.info("Highest bid for item {} is {} by user {}", closedSaleItem.getId(), winningBid.getBidAmount(), winningBid.getBuyer().getId());

            // Update SaleItem status (optional, could add SOLD_BIDDING status)
             // closedSaleItem.setBidStatus(SaleItem.BidStatus.SOLD); // Consider implications
             // saleItemRepository.save(closedSaleItem);

            // Notify the winner
            notificationService.createNotification(
                    winningBid.getBuyer(),
                    "Congratulations! You won the bid for " + closedSaleItem.getItemName() + " with a bid of " + winningBid.getBidAmount(),
                    Notification.NotificationType.BID_WON,
                    closedSaleItem.getId()
            );

            // Notify the supplier
            notificationService.createNotification(
                    closedSaleItem.getSupplier(),
                    "Bidding closed for " + closedSaleItem.getItemName() + ". Winning bid is " + winningBid.getBidAmount() + " by " + winningBid.getBuyer().getEmail(),
                    Notification.NotificationType.SALE_CLOSED, // Or a more specific type
                    closedSaleItem.getId()
            );

            // Notify losing bidders
            List<Bid> allBids = bidRepository.findBySaleItem(closedSaleItem);
            Set<String> bidderIds = allBids.stream().map(bid -> bid.getBuyer().getId()).collect(Collectors.toSet());

            for (String bidderId : bidderIds) {
                if (!bidderId.equals(winningBid.getBuyer().getId())) {
                    // Find the User object for the losing bidder (or fetch from repo if needed)
                     allBids.stream()
                         .filter(b -> b.getBuyer().getId().equals(bidderId))
                         .findFirst() // Get the first bid from this user to get their User object
                         .ifPresent(losingBid -> notificationService.createNotification(
                             losingBid.getBuyer(),
                             "Bidding for " + closedSaleItem.getItemName() + " has closed. Your bid was not the highest.",
                             Notification.NotificationType.BID_LOST,
                             closedSaleItem.getId()
                         ));
                }
            }

        } else {
            log.info("No bids were placed for item {}. Bidding closed.", closedSaleItem.getId());
            // Notify the supplier that no bids were placed
            notificationService.createNotification(
                    closedSaleItem.getSupplier(),
                    "Bidding closed for " + closedSaleItem.getItemName() + ". No bids were placed.",
                    Notification.NotificationType.SALE_CLOSED,
                    closedSaleItem.getId()
            );
             // Optionally update SaleItem status to something like UNSOLD or RELIST?
        }
    }
}
