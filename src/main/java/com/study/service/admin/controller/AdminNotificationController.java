package com.study.service.admin.controller;

import com.study.service.notification.dto.NotificationRequest;
import com.study.service.notification.dto.NotificationResponse;
import com.study.service.notification.service.NotificationService;
import com.study.service.security.CustomUserDetails;
import com.study.service.user.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/notifications")
public class AdminNotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public AdminNotificationController(NotificationService notificationService,
                                       UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    // 🔥 관리자: 알림 발송
    @PostMapping
    public List<NotificationResponse> sendNotification(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody NotificationRequest request
    ) {
        // 권한 체크
        if (!userDetails.isAdmin()) {
            throw new AccessDeniedException("관리자만 알림을 발송할 수 있습니다.");
        }

        List<Long> userIds = request.getUserIds();

        // ⭐ 전체 발송: userIds 비어있으면 전체 사용자
        if (userIds == null || userIds.isEmpty()) {
            userIds = userRepository.findAll()
                    .stream()
                    .map(u -> u.getUserId())
                    .toList();
        }

        return userIds.stream()
                .map(id -> notificationService.save(id, request))
                .toList();
    }
}
