package com.study.service.attendance.controller;

import com.study.service.attendance.dto.AttendanceRequest;
import com.study.service.attendance.dto.AttendanceResponse;
import com.study.service.attendance.dto.AttendanceStatusUpdateRequest;
import com.study.service.attendance.service.AttendanceService;
import com.study.service.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    // 🔹 출석 전체 조회: GET /api/attendance (관리자 전용)
    @GetMapping
    public ResponseEntity<List<AttendanceResponse>> getAll(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        System.out.println("[AttendanceController] GET /api/attendance 호출됨");

        if (user == null) {
            System.out.println("[AttendanceController] getAll: user == null → 403 반환");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        System.out.println("[AttendanceController] getAll: username=" + user.getUsername()
                + ", userId=" + user.getUserId()
                + ", isAdmin=" + user.isAdmin()
                + ", authorities=" + user.getAuthorities());

        if (!user.isAdmin()) {
            System.out.println("[AttendanceController] getAll: ADMIN 아님 → 403 반환");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<AttendanceResponse> result = attendanceService.findAll();
        System.out.println("[AttendanceController] getAll: 결과 size = " + result.size());

        return ResponseEntity.ok(result);
    }

    // 🔹 내 출석 전체 조회: GET /api/attendance/me
    @GetMapping("/me")
    public ResponseEntity<List<AttendanceResponse>> getMyAttendance(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        System.out.println("[AttendanceController] GET /api/attendance/me 진입!");

        if (user == null) {
            System.out.println("[AttendanceController] /me: user == null → 401 반환");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        System.out.println("[AttendanceController] /me: username=" + user.getUsername()
                + ", userId=" + user.getUserId()
                + ", authorities=" + user.getAuthorities());

        Long loginUserId = user.getUserId();
        System.out.println("[AttendanceController] /me 요청 – loginUserId = " + loginUserId);

        List<AttendanceResponse> result = attendanceService.findByUser(loginUserId);
        System.out.println("[AttendanceController] /me 결과 size = " + result.size());

        return ResponseEntity.ok(result);
    }

    // 🔹 특정 사용자의 출석 기록 전체 조회:
    // GET /api/attendance/user/{userId}
    // → 본인 or ADMIN
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AttendanceResponse>> getByUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        System.out.println("[AttendanceController] GET /api/attendance/user/" + userId + " 진입!");

        if (user == null) {
            System.out.println("[AttendanceController] /user/{userId}: user == null → 401 반환");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long loginUserId = user.getUserId();
        boolean isAdmin = user.isAdmin();

        System.out.println("[AttendanceController] /user/" + userId +
                " 요청 – loginUserId=" + loginUserId +
                ", isAdmin=" + isAdmin +
                ", authorities=" + user.getAuthorities());

        if (!isAdmin && !loginUserId.equals(userId)) {
            System.out.println("[AttendanceController] /user/{userId}: 권한 없음 → 403 반환");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<AttendanceResponse> result = attendanceService.findByUser(userId);
        System.out.println("[AttendanceController] /user/{userId} 결과 size = " + result.size());

        return ResponseEntity.ok(result);
    }

    // 🔹 스케줄별 출석 조회 (리더만):
    // GET /api/attendance/schedule/{scheduleId}
    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<List<AttendanceResponse>> getBySchedule(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        System.out.println("[AttendanceController] GET /api/attendance/schedule/" + scheduleId + " 진입!");

        if (user == null) {
            System.out.println("[AttendanceController] /schedule/{scheduleId}: user == null → 401 반환");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long leaderId = user.getUserId();
        System.out.println("[AttendanceController] /schedule/{scheduleId}: leaderId = " + leaderId);

        List<AttendanceResponse> result = attendanceService.findByScheduleForLeader(scheduleId, leaderId);
        System.out.println("[AttendanceController] /schedule/{scheduleId} 결과 size = " + result.size());

        return ResponseEntity.ok(result);
    }

    // 🔹 출석 기록 생성/갱신 (리더만): POST /api/attendance
    @PostMapping
    public ResponseEntity<AttendanceResponse> recordAttendance(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody AttendanceRequest request
    ) {
        System.out.println("[AttendanceController] POST /api/attendance 진입!");

        if (user == null) {
            System.out.println("[AttendanceController] POST /api/attendance: user == null → 401 반환");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long leaderId = user.getUserId();
        System.out.println("[AttendanceController] POST /api/attendance: leaderId = " + leaderId
                + ", request.scheduleId = " + request.getScheduleId()
                + ", request.userId = " + request.getUserId()
                + ", request.status = " + request.getStatus());

        AttendanceResponse response = attendanceService.checkIn(request, leaderId);
        System.out.println("[AttendanceController] POST /api/attendance 성공 – response = " + response);

        return ResponseEntity.ok(response);
    }

    // 🔹 출석 상태 변경 (리더만): PATCH /api/attendance/{attendanceId}
    @PatchMapping("/{attendanceId}")
    public ResponseEntity<AttendanceResponse> updateStatus(
            @PathVariable Long attendanceId,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody AttendanceStatusUpdateRequest request
    ) {
        System.out.println("[AttendanceController] PATCH /api/attendance/" + attendanceId + " 진입!");

        if (user == null) {
            System.out.println("[AttendanceController] PATCH /api/attendance/{attendanceId}: user == null → 401 반환");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long leaderId = user.getUserId();
        System.out.println("[AttendanceController] PATCH /api/attendance/{attendanceId}: leaderId = " + leaderId
                + ", newStatus = " + request.getStatus());

        AttendanceResponse response = attendanceService.updateStatus(attendanceId, request, leaderId);
        System.out.println("[AttendanceController] PATCH /api/attendance/{attendanceId} 성공 – response = " + response);

        return ResponseEntity.ok(response);
    }
}