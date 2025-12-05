package com.study.service.user.controller;

import com.study.service.user.dto.MannerScoreResponse;
import com.study.service.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manners")
@RequiredArgsConstructor
public class MannerController {

    private final UserService userService;

    // ============================
    // GET /api/manners/{userId}
    // 매너 점수 조회
    // ============================
    @GetMapping("/{userId}")
    public ResponseEntity<MannerScoreResponse> getMannerScore(@PathVariable Long userId) {
        MannerScoreResponse response = userService.getMannerScore(userId);
        return ResponseEntity.ok(response);
    }
}
