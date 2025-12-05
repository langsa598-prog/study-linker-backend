package com.study.service.notification.controller;

import com.study.service.notification.dto.NotificationRequest;
import com.study.service.notification.dto.NotificationResponse;
import com.study.service.notification.service.NotificationService;
import com.study.service.security.CustomUserDetails;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    // 🔹 GET /api/notifications - 내 알림 목록 조회
    @GetMapping
    public List<NotificationResponse> getAll(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        return service.findAllResponsesByUser(userId);
    }

    // 🔹 GET /api/notifications/unread - 내 읽지 않은 알림 조회
    @GetMapping("/unread")
    public List<NotificationResponse> getUnread(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        return service.findUnreadResponsesByUser(userId);
    }

    // 🔹 POST /api/notifications - 알림 생성 (✅ 관리자만, 대상 userId는 Request 에서 받음)
    @PostMapping
    public List<NotificationResponse> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody NotificationRequest request
    ) {
        // ✅ 관리자만 가능
        if (!userDetails.isAdmin()) {
            throw new AccessDeniedException("알림 생성은 관리자만 가능합니다.");
        }

        // ✅ 대상 유저 리스트 확인
        List<Long> userIds = request.getUserIds();
        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("알림 대상 userIds는 최소 1명 이상 필요합니다.");
        }

        // ✅ 여러 명에게 알림 생성
        return userIds.stream()
                .map(userId -> service.save(userId, request))
                .toList();
    }

    // 🔹 PATCH /api/notifications/{id}/read - 내 알림 읽음 처리
    @PatchMapping("/{id}/read")
    public NotificationResponse markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        return service.markAsRead(id, userId);
    }

    // 🔹 DELETE /api/notifications/{id} - 내 알림 단건 삭제
    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        service.deleteById(id, userId);
    }

    // 🔹 DELETE /api/notifications/all - 내 알림 전체 삭제
    @DeleteMapping("/all")
    public void deleteAll(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        service.deleteAllByUser(userId);
    }
}