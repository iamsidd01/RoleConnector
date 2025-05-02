package com.example.agrisupply.service;

import com.example.agrisupply.model.Notification;
import com.example.agrisupply.model.User;
import com.example.agrisupply.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final AuthService authService; // To get current user for fetching notifications

    /**
     * Creates and saves a new notification.
     * In a real application, this might also trigger push notifications, emails, etc.
     *
     * @param recipient     The user who should receive the notification.
     * @param message       The notification message content.
     * @param type          The type of notification.
     * @param relatedItemId The ID of the entity related to the notification (e.g., SaleItem ID).
     * @return The saved Notification entity.
     */
    public Notification createNotification(User recipient, String message, Notification.NotificationType type, String relatedItemId) {
        log.info("Creating notification for user {}: {}", recipient.getEmail(), message);
        Notification notification = new Notification(recipient, message, type, relatedItemId);
        // Here you could add logic to send real-time notifications (WebSockets, SSE, email, etc.)
        return notificationRepository.save(notification);
    }

    /**
     * Fetches all notifications for the currently authenticated user, sorted by creation time descending.
     *
     * @return A list of Notification entities.
     */
    public List<Notification> getMyNotifications() {
        User currentUser = authService.getCurrentUserOrFail();
        return notificationRepository.findByRecipient(currentUser, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
    }

     /**
      * Fetches only unread notifications for the currently authenticated user, sorted by creation time descending.
      *
      * @return A list of unread Notification entities.
      */
    public List<Notification> getMyUnreadNotifications() {
        User currentUser = authService.getCurrentUserOrFail();
        return notificationRepository.findUnreadNotificationsForUserSorted(currentUser);
    }

    /**
     * Marks a specific notification as read.
     *
     * @param notificationId The ID of the notification to mark as read.
     * @return true if the notification was found and marked as read, false otherwise.
     * @throws com.example.agrisupply.exception.ForbiddenAccessException if the user does not own the notification.
     * @throws com.example.agrisupply.exception.NotFoundException if the notification is not found.
     */
    public boolean markNotificationAsRead(String notificationId) {
        User currentUser = authService.getCurrentUserOrFail();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new com.example.agrisupply.exception.NotFoundException("Notification not found with ID: " + notificationId));

        // Ensure the current user is the recipient of the notification
        if (!notification.getRecipient().getId().equals(currentUser.getId())) {
            throw new com.example.agrisupply.exception.ForbiddenAccessException("You can only mark your own notifications as read.");
        }

        if (!notification.isRead()) {
            notification.setRead(true);
            notificationRepository.save(notification);
            log.info("Marked notification {} as read for user {}", notificationId, currentUser.getEmail());
            return true;
        }
        return false; // Already read
    }

     /**
      * Marks all unread notifications for the current user as read.
      * @return The number of notifications marked as read.
      */
     public long markAllMyNotificationsAsRead() {
         User currentUser = authService.getCurrentUserOrFail();
         List<Notification> unreadNotifications = notificationRepository.findByRecipientAndIsRead(currentUser, false);
         if (unreadNotifications.isEmpty()) {
             return 0;
         }
         for (Notification notification : unreadNotifications) {
             notification.setRead(true);
         }
         notificationRepository.saveAll(unreadNotifications);
         log.info("Marked {} notifications as read for user {}", unreadNotifications.size(), currentUser.getEmail());
         return unreadNotifications.size();
     }
}
