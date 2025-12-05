package com.study.service.studyschedule.controller;

import com.study.service.security.CustomUserDetails;
import com.study.service.studyschedule.domain.StudySchedule;
import com.study.service.studyschedule.service.StudyScheduleService;
import com.study.service.studyschedule.dto.MyScheduleResponse;
import com.study.service.studyschedule.dto.StudyScheduleRequest;
import com.study.service.studyschedule.dto.StudyScheduleResponse;
import com.study.service.studyschedule.dto.StudyScheduleStatusUpdateRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.study.service.config.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;


import java.util.List;

@RestController
@RequestMapping("/api/study-schedules")
public class StudyScheduleController {

    private final StudyScheduleService service;
    private final JwtTokenProvider jwtTokenProvider;   // ✅ 추가
    public StudyScheduleController(StudyScheduleService service,
                                   JwtTokenProvider jwtTokenProvider) { // ✅ 생성자 파라미터 추가
        this.service = service;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // ✅ 일정 단건 조회: GET /api/study-schedules/{scheduleId} (공통)
    @GetMapping("/{scheduleId}")
    public StudyScheduleResponse getById(@PathVariable Long scheduleId) {
        StudySchedule schedule = service.findById(scheduleId);
        return new StudyScheduleResponse(schedule);
    }

    // ✅ 개인 일정 생성: POST /api/study-schedules (일반 사용자)
    @PostMapping
    public StudyScheduleResponse create(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody StudyScheduleRequest request
    ) {
        Long ownerId = user.getUserId();
        StudySchedule schedule = service.save(request, ownerId);
        return new StudyScheduleResponse(schedule);
    }

    // ✅ 일정 수정: PUT /api/study-schedules/{scheduleId}
    // → 일정 주인 OR 그룹 리더만
    @PutMapping("/{scheduleId}")
    public StudyScheduleResponse update(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody StudyScheduleRequest request
    ) {
        Long loginUserId = user.getUserId();
        StudySchedule schedule = service.update(scheduleId, request, loginUserId);
        return new StudyScheduleResponse(schedule);
    }

    // ✅ 일정 상태 변경: PATCH /api/study-schedules/{scheduleId}/status
    // → 해당 그룹 리더만
    @PatchMapping("/{scheduleId}/status")
    public StudyScheduleResponse updateStatus(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody StudyScheduleStatusUpdateRequest request
    ) {
        Long loginUserId = user.getUserId();
        StudySchedule schedule = service.updateStatus(scheduleId, request, loginUserId);
        return new StudyScheduleResponse(schedule);
    }

    // ✅ 일정 삭제: DELETE /api/study-schedules/{scheduleId}
    // → 일정 주인 OR 그룹 리더
    @DeleteMapping("/{scheduleId}")
    public void delete(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long loginUserId = user.getUserId();
        service.deleteById(scheduleId, loginUserId);
    }

    // 📌 내 전체 일정 조회: GET /api/study-schedules/me (일반 사용자)
    @GetMapping("/me")
    public List<MyScheduleResponse> getMySchedules(HttpServletRequest request) {

        System.out.println(">>> [DEBUG] [/me] 컨트롤러 진입");

        String header = request.getHeader("Authorization");
        System.out.println(">>> [DEBUG] Authorization = " + header);

        if (header == null || !header.startsWith("Bearer ")) {
            System.out.println(">>> [DEBUG] 토큰 없음 → 401");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        String token = header.substring(7);
        System.out.println(">>> [DEBUG] token 일부 = " + token.substring(0, 20) + "...");

        boolean valid = jwtTokenProvider.validateToken(token);
        System.out.println(">>> [DEBUG] token valid = " + valid);

        Long userId = jwtTokenProvider.getUserId(token);
        System.out.println(">>> [DEBUG] token userId = " + userId);

        System.out.println(">>> [DEBUG] 서비스 호출 직전");
        List<MyScheduleResponse> result = service.getMySchedules(userId);
        System.out.println(">>> [DEBUG] 서비스 호출 성공, result.size = " + result.size());

        return result;
    }


}