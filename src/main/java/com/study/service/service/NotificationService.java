package com.study.service.service;

import com.study.service.domain.notification.Notification;
import com.study.service.domain.user.User;
import com.study.service.repository.NotificationRepository;
import com.study.service.repository.UserRepository;
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

    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    @Transactional
    public Notification save(Notification notification) {
        // userId로 User 조회
        Long userId = notification.getUser().getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        notification.setUser(user); // 영속 상태 User로 세팅
        return notificationRepository.save(notification);
    }

    public void deleteById(Long id) {
        notificationRepository.deleteById(id);
    }
}