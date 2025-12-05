package com.study.service.auth.service;

import com.study.service.user.domain.User;
import com.study.service.user.domain.Role;
import com.study.service.user.repository.UserRepository;
import com.study.service.config.JwtTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       JwtTokenProvider jwtTokenProvider,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    // ============================
    // 회원가입
    // ============================
    @Transactional
    public User signupUser(User user) {

        // 아이디 중복 체크
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다: " + user.getUsername());
        }

        // 이메일 중복 체크 (null 아닐 때만)
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + user.getEmail());
        }

        // 비밀번호 암호화
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 기본 ROLE 설정 (없으면 USER)
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        return userRepository.save(user);
    }

    // ============================
    // 로그인 (JWT 발급)
    // ============================
    public String login(String username, String password) {
        // 디버그용 출력 (원하면 나중에 제거해도 됨)
        System.out.println("입력한 아이디: " + username + " / 입력한 비번: " + password);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        System.out.println("DB에 저장된 해시: " + user.getPassword());
        System.out.println("matches 결과: " + passwordEncoder.matches(password, user.getPassword()));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 토큰 생성
        return jwtTokenProvider.createToken(
                user.getUsername(),
                user.getRole().name(),
                user.getUserId()
        );
    }

    // ============================
    // 아이디 존재 여부 확인
    // ============================
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // ============================
    // 로그아웃 (토큰 무효화)
    // ============================
    public void logout(String token) {
        // "Bearer xxx" 형태가 아닌, 이미 순수 토큰 문자열만 들어온다고 가정
        jwtTokenProvider.invalidateToken(token);
    }
}