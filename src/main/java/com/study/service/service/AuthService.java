package com.study.service.service;

import com.study.service.domain.user.User;
import com.study.service.domain.user.Role;
import com.study.service.repository.UserRepository;
import com.study.service.config.JwtTokenProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder; // 타입 변경

    public AuthService(UserRepository userRepository,
                       JwtTokenProvider jwtTokenProvider,
                       PasswordEncoder passwordEncoder) { // 타입 변경
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입
    public User signupUser(User user) {
        if(userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("이미 존재하는 사용자입니다.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    // 로그인 (JWT 발급)
    public String login(String username, String password) {
        // 디버그용 출력
        System.out.println("입력한 아이디: " + username + " / 입력한 비번: " + password);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // DB에서 꺼낸 해시값 출력
        System.out.println("DB에 저장된 해시: " + user.getPassword());
        System.out.println("matches 결과: " + passwordEncoder.matches(password, user.getPassword()));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 토큰 생성
        return jwtTokenProvider.createToken(user.getUsername(), user.getRole().name());
    }

    // 아이디 존재 여부 확인
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}