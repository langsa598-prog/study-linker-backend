package com.study.service.studyschedule.dto;

public class StudyScheduleRequest {
    private Long groupId;
    private String title;
    private String description;
    private String startTime; // "yyyy-MM-dd HH:mm:ss" 형식
    private String endTime;   // "yyyy-MM-dd HH:mm:ss" 형식
    private String location;

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}