package com.study.service.notification.service;

import com.study.service.notification.domain.Notification;
import com.study.service.notification.dto.NotificationRequest;
import com.study.service.notification.dto.NotificationResponse;
import com.study.service.notification.repository.NotificationRepository;
import com.study.service.user.domain.User;
import com.study.service.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    // 🔹 로그인 유저의 전체 알림 조회
    @Transactional(readOnly = true)
    public List<NotificationResponse> findAllResponsesByUser(Long userId) {
        return notificationRepository
                .findByUser_UserIdOrderByNotificationIdDesc(userId)   // ✅ 메서드명 수정
                .stream()
                .map(NotificationResponse::fromEntity)
                .toList();
    }

    // 🔹 로그인 유저의 읽지 않은 알림 조회
    @Transactional(readOnly = true)
    public List<NotificationResponse> findUnreadResponsesByUser(Long userId) {
        return notificationRepository
                .findByUser_UserIdAndIsReadFalseOrderByNotificationIdDesc(userId)  // ✅ 메서드명 수정
                .stream()
                .map(NotificationResponse::fromEntity)
                .toList();
    }

    // 🔹 알림 생성 (userId = 알림 받을 유저 ID)
    @Transactional
    public NotificationResponse save(Long userId, NotificationRequest request) {

        // ✅ userId = 알림을 받을 대상 사용자 ID
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(request.getMessage());
        notification.setType(Notification.Type.valueOf(request.getType().toUpperCase()));
        notification.setIsRead(false); // 생성 시 기본값: 읽지 않음

        Notification saved = notificationRepository.save(notification);
        return NotificationResponse.fromEntity(saved);
    }

    // 🔹 알림 읽음 처리 (단건) - 내 알림만
    @Transactional
    public NotificationResponse markAsRead(Long id, Long userId) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("알림을 찾을 수 없습니다. ID: " + id));

        // 내 알림인지 체크
        if (!notification.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인의 알림만 읽음 처리할 수 있습니다.");
        }

        notification.setIsRead(true);
        return NotificationResponse.fromEntity(notification);
    }

    // 🔹 알림 단건 삭제 - 내 알림만
    @Transactional
    public void deleteById(Long id, Long userId) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("알림을 찾을 수 없습니다. ID: " + id));

        if (!notification.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인의 알림만 삭제할 수 있습니다.");
        }

        notificationRepository.delete(notification);
    }

    // 🔹 로그인 유저의 알림 전체 삭제
    @Transactional
    public void deleteAllByUser(Long userId) {
        notificationRepository.deleteByUser_UserId(userId);
    }
}