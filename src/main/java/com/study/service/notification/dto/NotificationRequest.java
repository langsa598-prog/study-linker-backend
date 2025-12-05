package com.study.service.notification.dto;

import java.util.List;

public class NotificationRequest {

    // ✅ 여러 명에게 보낼 수 있도록 변경
    private List<Long> userIds;

    private String message;
    private String type; // "SCHEDULE", "REQUEST", "SYSTEM"

    public List<Long> getUserIds() { return userIds; }
    public void setUserIds(List<Long> userIds) { this.userIds = userIds; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}