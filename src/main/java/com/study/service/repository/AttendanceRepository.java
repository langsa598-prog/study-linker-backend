package com.study.service.repository;

import com.study.service.domain.attendance.Attendance;
import com.study.service.domain.studySchedule.StudySchedule;
import com.study.service.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByScheduleAndUser(StudySchedule schedule, User user);
    List<Attendance> findBySchedule_ScheduleId(Long scheduleId);
}