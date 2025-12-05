package com.study.service.attendance.repository;

import com.study.service.attendance.domain.Attendance;
import com.study.service.studyschedule.domain.StudySchedule;
import com.study.service.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByScheduleAndUser(StudySchedule schedule, User user);

    // 스케줄별 조회 (기존 - 필요하면 유지)
    List<Attendance> findBySchedule_ScheduleId(Long scheduleId);

    // 사용자별 조회 (새로 추가: GET /api/attendance/user/{userId})
    List<Attendance> findByUser_UserId(Long userId);
}