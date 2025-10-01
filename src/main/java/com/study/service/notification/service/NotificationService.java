package com.study.service.notification.service;

import com.study.service.notification.domain.Notification;
import com.study.service.notification.dto.NotificationRequest;
import com.study.service.notification.dto.NotificationResponse;
import com.study.service.user.domain.User;
import com.study.service.user.repository.UserRepository;
import com.study.service.notification.repository.NotificationRepository;
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

    // GET 요청용
    public List<NotificationResponse> findAllResponses() {
        return notificationRepository.findAll().stream()
                .map(n -> new NotificationResponse(
                        n.getNotificationId(),
                        n.getUser().getUserId(),
                        n.getMessage(),
                        n.getType().name(),
                        n.getIsRead(),
                        n.getCreatedAt()
                ))
                .toList();
    }

    // POST 요청용:
    @Transactional
    public NotificationResponse save(NotificationRequest request) {
        // userId로 User 조회
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + request.getUserId()));

        // Notification 생성 및 세팅
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(request.getMessage());
        notification.setType(Notification.Type.valueOf(request.getType())); // 문자열 → enum
        notification.setIsRead(request.getIsRead() != 0); // int → Boolean

        Notification saved = notificationRepository.save(notification);
        
        return new NotificationResponse(
                saved.getNotificationId(),
                saved.getUser().getUserId(),
                saved.getMessage(),
                saved.getType().name(),
                saved.getIsRead(),
                saved.getCreatedAt()
        );
    }

    public void deleteById(Long id) {
        notificationRepository.deleteById(id);
    }
}