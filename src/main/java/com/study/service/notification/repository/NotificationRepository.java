package com.study.service.notification.repository;

import com.study.service.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // ✅ 로그인한 유저의 전체 알림 조회 (최신순)
    List<Notification> findByUser_UserIdOrderByNotificationIdDesc(Long userId);

    // ✅ 로그인한 유저의 읽지 않은 알림 조회 (최신순)
    List<Notification> findByUser_UserIdAndIsReadFalseOrderByNotificationIdDesc(Long userId);

    // ✅ 로그인한 유저의 모든 알림 삭제
    void deleteByUser_UserId(Long userId);
}