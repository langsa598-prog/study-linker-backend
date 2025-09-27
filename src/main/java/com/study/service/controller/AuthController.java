package com.study.service.controller;

import com.study.service.domain.user.User;
import com.study.service.domain.user.Role;
import com.study.service.domain.user.dto.UserRequest;
import com.study.service.domain.user.dto.UserResponse;
import com.study.service.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 안내 GET 엔드포인트
    @GetMapping("/signup")
    public String signupInfo() {
        return "일반 회원가입 API입니다. POST 요청과 JSON Body를 사용하세요.";
    }

    @GetMapping("/signup/admin")
    public String signupAdminInfo() {
        return "관리자 회원가입 API입니다. POST 요청과 JSON Body를 사용하세요.";
    }

    // 일반 회원가입
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody UserRequest request) {
        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            user.setEmail(request.getEmail());
            user.setName(request.getName());
            user.setRole(Role.USER); // 일반 회원
            user.setLatitude(request.getLatitude());
            user.setLongitude(request.getLongitude());
            user.setInterestTags(request.getInterestTags());

            // 회원가입
            User savedUser = authService.signupUser(user);

            // 응답 DTO
            UserResponse response = new UserResponse();
            response.setUserId(savedUser.getUserId());
            response.setUsername(savedUser.getUsername());
            response.setEmail(savedUser.getEmail());
            response.setName(savedUser.getName());
            response.setRole(savedUser.getRole());
            response.setLatitude(savedUser.getLatitude());
            response.setLongitude(savedUser.getLongitude());
            response.setInterestTags(savedUser.getInterestTags());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequest request) {
        try {
            String token = authService.login(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}

// JWT 응답 DTO
class JwtResponse {
    private String token;

    public JwtResponse(String token) { this.token = token; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}