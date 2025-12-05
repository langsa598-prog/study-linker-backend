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
    private String status;
    private LocalDateTime createdAt;

    // ✅ 기존 생성자 그대로 유지
    public StudyScheduleResponse(StudySchedule schedule) {
        this.scheduleId = schedule.getScheduleId();

        // group이 null일 수도 있으니 방어적으로
        if (schedule.getGroup() != null) {
            this.groupId = schedule.getGroup().getGroupId();
        }

        this.title = schedule.getTitle();
        this.description = schedule.getDescription();
        this.startTime = schedule.getStartTime();
        this.endTime = schedule.getEndTime();
        this.location = schedule.getLocation();

        // ⭐ Enum → String 변환
        this.status = (schedule.getStatus() != null)
                ? schedule.getStatus().name()
                : null;

        this.createdAt = schedule.getCreatedAt();
    }

    // ⭐ 추가: 서비스/컨트롤러에서 편하게 쓰라고 fromEntity 제공
    public static StudyScheduleResponse fromEntity(StudySchedule schedule) {
        return new StudyScheduleResponse(schedule);
    }

    public Long getScheduleId() { return scheduleId; }
    public Long getGroupId() { return groupId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public String getLocation() { return location; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}