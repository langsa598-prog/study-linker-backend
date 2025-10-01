package com.study.service.studyschedule.dto;

import com.study.service.studyschedule.domain.StudySchedule;

import java.time.LocalDateTime;

public class StudyScheduleResponse {
    private Long scheduleId;
    private Long groupId;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private LocalDateTime createdAt;

    public StudyScheduleResponse(StudySchedule schedule) {
        this.scheduleId = schedule.getScheduleId();
        this.groupId = schedule.getGroup().getGroupId();
        this.title = schedule.getTitle();
        this.description = schedule.getDescription();
        this.startTime = schedule.getStartTime();
        this.endTime = schedule.getEndTime();
        this.location = schedule.getLocation();
        this.createdAt = schedule.getCreatedAt();
    }

    // Getter
    public Long getScheduleId() { return scheduleId; }
    public Long getGroupId() { return groupId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public String getLocation() { return location; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
