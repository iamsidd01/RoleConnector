package com.example.agrisupply.repository;

import com.example.agrisupply.model.Notification;
import com.example.agrisupply.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByRecipient(User recipient);
    List<Notification> findByRecipientAndIsRead(User recipient, boolean isRead);
    List<Notification> findByRecipient(User recipient, Sort sort); // e.g., sort by createdAt descending

    // Find unread notifications for a user, sorted by most recent first
    default List<Notification> findUnreadNotificationsForUserSorted(User recipient) {
        return findByRecipientAndIsRead(recipient, false, Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
