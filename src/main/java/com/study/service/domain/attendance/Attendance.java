package com.study.service.domain.attendance;

import com.study.service.domain.studySchedule.StudySchedule;
import com.study.service.domain.user.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"schedule_id", "user_id"})})
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attendanceId;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private StudySchedule schedule;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ABSENT;

    @Column(name = "checked_at", updatable = false)
    private LocalDateTime checkedAt = LocalDateTime.now();

    public enum Status {
        PRESENT, ABSENT, LATE
    }

    public Long getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Long attendanceId) {
        this.attendanceId = attendanceId;
    }

    public StudySchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(StudySchedule schedule) {
        this.schedule = schedule;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(LocalDateTime checkedAt) {
        this.checkedAt = checkedAt;
    }
}
