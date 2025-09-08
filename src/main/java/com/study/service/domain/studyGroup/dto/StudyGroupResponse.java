package com.study.service.domain.studyGroup.dto;

import java.time.LocalDateTime;

public class StudyGroupResponse {
    private Long id;
    private Long leaderId;
    private String title;
    private String description;
    private int maxMembers;
    private String category;
    private double latitude;
    private double longitude;
    private LocalDateTime createdAt;

    public StudyGroupResponse(Long id, Long leaderId, String title, String description,
                              int maxMembers, String category, double latitude, double longitude,
                              LocalDateTime createdAt) {
        this.id = id;
        this.leaderId = leaderId;
        this.title = title;
        this.description = description;
        this.maxMembers = maxMembers;
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getLeaderId() { return leaderId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getMaxMembers() { return maxMembers; }
    public String getCategory() { return category; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}