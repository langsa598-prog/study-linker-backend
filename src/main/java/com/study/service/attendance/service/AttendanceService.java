package com.study.service.attendance.service;

import com.study.service.attendance.domain.Attendance;
import com.study.service.attendance.dto.AttendanceRequest;
import com.study.service.attendance.dto.AttendanceResponse;
import com.study.service.attendance.dto.AttendanceStatusUpdateRequest;
import com.study.service.attendance.repository.AttendanceRepository;
import com.study.service.studyschedule.domain.StudySchedule;
import com.study.service.studyschedule.repository.StudyScheduleRepository;
import com.study.service.user.domain.User;
import com.study.service.user.repository.UserRepository;
import com.study.service.user.service.MannerScoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    private final AttendanceRepository repository;
    private final StudyScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final MannerScoreService mannerScoreService; // ✅ 매너 점수 서비스

    public AttendanceService(AttendanceRepository repository,
                             StudyScheduleRepository scheduleRepository,
                             UserRepository userRepository,
                             MannerScoreService mannerScoreService) {
        this.repository = repository;
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
        this.mannerScoreService = mannerScoreService;
    }

    // 전체 조회 (관리자만 컨트롤러에서 허용)
    @Transactional(readOnly = true)
    public List<AttendanceResponse> findAll() {
        return repository.findAll().stream()
                .map(AttendanceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // ✅ 상태별 매너 점수 값
    private int getScoreByStatus(Attendance.Status status) {
        if (status == null) return 0;
        return switch (status) {
            case PRESENT -> 2;   // 출석
            case LATE    -> 1;   // 지각
            case ABSENT  -> -2;  // 결석
            default      -> 0;
        };
    }

    // ✅ 리더 여부 체크 (scheduleId 기준)
    private boolean isLeaderOfSchedule(Long scheduleId, Long userId) {
        StudySchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 스케줄 ID입니다."));

        if (schedule.getGroup() == null || schedule.getGroup().getLeader() == null) {
            return false;
        }
        return schedule.getGroup().getLeader().getUserId().equals(userId);
    }

    // ✅ 출석 체크(있으면 업데이트, 없으면 생성) + 매너 점수 반영
    // → 리더만 가능
    @Transactional
    public AttendanceResponse checkIn(AttendanceRequest request, Long leaderId) {
        if (!isLeaderOfSchedule(request.getScheduleId(), leaderId)) {
            throw new SecurityException("해당 일정의 리더만 출석을 기록할 수 있습니다.");
        }

        StudySchedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 스케줄 ID입니다."));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 유저 ID입니다."));

        Optional<Attendance> existing = repository.findByScheduleAndUser(schedule, user);

        Attendance attendance;
        Attendance.Status oldStatus = null;
        Attendance.Status newStatus = Attendance.Status.valueOf(request.getStatus());

        if (existing.isPresent()) {
            // 기존 기록 있으면 → 상태/시간 갱신
            attendance = existing.get();
            oldStatus = attendance.getStatus();
            attendance.setStatus(newStatus);
            attendance.setCheckedAt(LocalDateTime.now());
        } else {
            // 없으면 새로 생성
            attendance = new Attendance();
            attendance.setSchedule(schedule);
            attendance.setUser(user);
            attendance.setStatus(newStatus);
            attendance.setCheckedAt(LocalDateTime.now());
        }

        // ✅ 매너 점수 delta 계산 (새 상태 점수 - 기존 상태 점수)
        int oldScore = getScoreByStatus(oldStatus);
        int newScore = getScoreByStatus(newStatus);
        int delta = newScore - oldScore;

        if (delta != 0) {
            mannerScoreService.updateMannerScore(user.getUserId(), "attendance_score", delta);
        }

        Attendance saved = repository.save(attendance);
        return AttendanceResponse.fromEntity(saved);
    }

    // ✅ 스케줄별 조회 (리더만)
    @Transactional(readOnly = true)
    public List<AttendanceResponse> findByScheduleForLeader(Long scheduleId, Long leaderId) {
        if (!isLeaderOfSchedule(scheduleId, leaderId)) {
            throw new SecurityException("해당 일정의 리더만 출석 현황을 조회할 수 있습니다.");
        }

        return repository.findBySchedule_ScheduleId(scheduleId).stream()
                .map(AttendanceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 사용자별 조회: GET /api/attendance/user/{userId}, /api/attendance/me
    @Transactional(readOnly = true)
    public List<AttendanceResponse> findByUser(Long userId) {
        return repository.findByUser_UserId(userId).stream()
                .map(AttendanceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // ✅ 출석 상태 변경 (PATCH) + 매너 점수 반영 (리더만)
    @Transactional
    public AttendanceResponse updateStatus(Long attendanceId,
                                           AttendanceStatusUpdateRequest request,
                                           Long leaderId) {
        Attendance attendance = repository.findById(attendanceId)
                .orElseThrow(() -> new IllegalArgumentException("출석 기록을 찾을 수 없습니다."));

        Long scheduleId = attendance.getSchedule().getScheduleId();
        if (!isLeaderOfSchedule(scheduleId, leaderId)) {
            throw new SecurityException("해당 일정의 리더만 출석 상태를 수정할 수 있습니다.");
        }

        Attendance.Status oldStatus = attendance.getStatus();
        Attendance.Status newStatus = Attendance.Status.valueOf(request.getStatus());

        attendance.setStatus(newStatus);
        attendance.setCheckedAt(LocalDateTime.now());

        // ✅ 매너 점수 delta 계산
        int oldScore = getScoreByStatus(oldStatus);
        int newScore = getScoreByStatus(newStatus);
        int delta = newScore - oldScore;

        if (delta != 0 && attendance.getUser() != null) {
            Long userId = attendance.getUser().getUserId();
            mannerScoreService.updateMannerScore(userId, "attendance_score", delta);
        }

        return AttendanceResponse.fromEntity(repository.save(attendance));
    }
}