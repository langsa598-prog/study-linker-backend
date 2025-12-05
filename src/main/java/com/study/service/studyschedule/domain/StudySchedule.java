package com.study.service.studyschedule.domain;

import com.study.service.studygroup.domain.StudyGroup;
import com.study.service.user.domain.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Study_schedules")
public class StudySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    // ✅ 일정 주인 (개인 일정 / 리더 일정 모두)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ✅ 그룹은 있어도 되고 없어도 됨 (개인 일정 지원)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id") // nullable = true
    private StudyGroup group;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StudyScheduleStatus status = StudyScheduleStatus.SCHEDULED;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // ============================
    // Getter / Setter
    // ============================

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public User getUser() {           // ✅ 추가
        return user;
    }

    public void setUser(User user) {  // ✅ 추가
        this.user = user;
    }

    public StudyGroup getGroup() {
        return group;
    }

    public void setGroup(StudyGroup group) {
        this.group = group;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public StudyScheduleStatus getStatus() {
        return status;
    }

    public void setStatus(StudyScheduleStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}