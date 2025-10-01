package com.study.service.notification.domain;

import com.study.service.user.domain.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type = Type.SYSTEM;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Type {
        SCHEDULE, REQUEST, SYSTEM
    }

    // ===== Getter & Setter =====
    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Optional: toString() 편하게 디버깅용
    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", user=" + (user != null ? user.getUserId() : null) +
                ", message='" + message + '\'' +
                ", type=" + type +
                ", isRead=" + isRead +
                ", createdAt=" + createdAt +
                '}';
    }
}