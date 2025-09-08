package com.study.service.controller;

import com.study.service.domain.attendance.dto.AttendanceRequest;
import com.study.service.domain.attendance.dto.AttendanceResponse;
import com.study.service.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    // 전체 출석 조회
    @GetMapping
    public ResponseEntity<List<AttendanceResponse>> getAll() {
        return ResponseEntity.ok(attendanceService.findAll());
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<AttendanceResponse> getOne(@PathVariable Long id) {
        return attendanceService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 스케줄별 출석 조회
    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<List<AttendanceResponse>> getBySchedule(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(attendanceService.findBySchedule(scheduleId));
    }

    // 출석 체크 (기존 있으면 상태 변경 + 시간 갱신)
    @PostMapping
    public ResponseEntity<AttendanceResponse> recordAttendance(@RequestBody AttendanceRequest request) {
        return ResponseEntity.ok(attendanceService.checkIn(request));
    }

    // 출석 상태 변경
    @PutMapping("/{id}")
    public ResponseEntity<AttendanceResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        return ResponseEntity.ok(attendanceService.updateStatus(id, status));
    }

    // 출석 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteById(id);
        return ResponseEntity.ok("출석이 성공적으로 삭제되었습니다!");
    }
}