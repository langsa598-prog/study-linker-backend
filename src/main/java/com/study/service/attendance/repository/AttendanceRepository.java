package com.study.service.attendance.repository;

import com.study.service.attendance.domain.Attendance;
import com.study.service.studyschedule.domain.StudySchedule;
import com.study.service.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByScheduleAndUser(StudySchedule schedule, User user);
    List<Attendance> findBySchedule_ScheduleId(Long scheduleId);
}