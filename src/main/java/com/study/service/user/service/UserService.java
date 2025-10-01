package com.study.service.user.service;

import com.study.service.user.domain.User;
import com.study.service.user.dto.UserRequest;
import com.study.service.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + id));
    }

    public User save(UserRequest request) { // 새로운 User 객체를 만들어서 DB에 저장 -> 사용자 생성(Create)
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setName(request.getName());
        user.setRole(request.getRole());
        user.setInterestTags(request.getInterestTags());
        user.setLatitude(request.getLatitude());
        user.setLongitude(request.getLongitude());
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User update(Long id, UserRequest request) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(request.getUsername());
                    user.setPassword(request.getPassword());
                    user.setName(request.getName());
                    user.setEmail(request.getEmail());
                    user.setRole(request.getRole());
                    user.setInterestTags(request.getInterestTags());
                    user.setLatitude(request.getLatitude());
                    user.setLongitude(request.getLongitude());
                    user.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new IllegalArgumentException("수정된 사용자를 찾을 수 없습니다. ID: " + id));
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}