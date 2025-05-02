package com.example.agrisupply.dto;

import com.example.agrisupply.model.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Example DTO if you need to transform Notification entities for the API response
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private String id;
    private String message;
    private Notification.NotificationType type;
    private String relatedItemId;
    private LocalDateTime createdAt;
    private boolean isRead;

    // Factory method or Mapper can be used for conversion
    public static NotificationDto fromEntity(Notification notification) {
        return new NotificationDto(
                notification.getId(),
                notification.getMessage(),
                notification.getType(),
                notification.getRelatedItemId(),
                notification.getCreatedAt(),
                notification.isRead()
        );
    }
}
