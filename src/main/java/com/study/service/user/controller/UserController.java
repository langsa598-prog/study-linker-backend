package com.study.service.user.controller;

import com.study.service.security.CustomUserDetails;
import com.study.service.studygroup.domain.StudyGroup;
import com.study.service.studygroup.service.StudyGroupService;
import com.study.service.user.domain.User;
import com.study.service.user.dto.UserRequest;
import com.study.service.user.dto.UserResponse;
import com.study.service.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;
    private final StudyGroupService studyGroupService;   // ✅ 추가된 서비스

    public UserController(UserService service,
                          StudyGroupService studyGroupService) {
        this.service = service;
        this.studyGroupService = studyGroupService;
    }

    private boolean isSelf(Long pathUserId, CustomUserDetails userDetails) {

        if (userDetails == null) {
            System.out.println("❌ userDetails is null");
            return false;
        }

        System.out.println("✅ Path userId     = " + pathUserId);
        System.out.println("✅ Token userId    = " + userDetails.getUserId());

        return pathUserId != null
                && pathUserId.equals(userDetails.getUserId());
    }

    // =========================
    // ✅ GET /api/users/profile
    // =========================
    @GetMapping("/profile")
    public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        try {
            UserResponse profile = service.getProfile(userDetails.getUserId());

            System.out.println(">>> [DEBUG] Profile loaded: " + profile.getUsername());
            return ResponseEntity.ok(profile);

        } catch (Exception e) {
            System.err.println(">>> [ERROR] Profile API exception 발생");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("프로필 로드 중 서버 오류: " + e.getMessage());
        }
    }

    // =========================
    // ✅ 회원가입
    // =========================
    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody UserRequest request) {

        UserResponse created = service.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // =========================
    // ✅ PUT /api/users/{userId}
    // =========================
    @PutMapping("/{userId}")
    public ResponseEntity<?> update(@PathVariable Long userId,
                                    @RequestBody UserRequest request,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (!isSelf(userId, userDetails)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("본인의 정보만 수정할 수 있습니다.");
        }

        // ⭐ now returns User → convert to DTO manually
        User updated = service.update(userId, request);
        return ResponseEntity.ok(UserResponse.fromEntity(updated));
    }

    // =========================
    // ✅ DELETE /api/users/{userId}
    // =========================
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> delete(@PathVariable Long userId,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (!isSelf(userId, userDetails)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("본인 계정만 삭제할 수 있습니다.");
        }

        service.deleteById(userId);
        return ResponseEntity.ok("계정이 삭제되었습니다.");
    }

    // =========================
    // ✅ GET /api/users/{userId}/groups
    //    내가 참여한 스터디 그룹 목록
    // =========================
    @GetMapping("/{userId}/groups")
    public ResponseEntity<?> getJoinedGroups(@PathVariable Long userId,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        Long tokenUserId = userDetails.getUserId();

        // ⚠ path랑 토큰이 다르면 경고만 찍고, 실제 조회는 토큰 기준으로만 함
        if (!userId.equals(tokenUserId)) {
            System.out.println("⚠ [WARN] Path userId (" + userId +
                    ") != Token userId (" + tokenUserId + "), 토큰 기준으로만 조회합니다.");
        }

        // ⭐ 실제 조회는 무조건 토큰의 userId 기준으로
        List<StudyGroup> groups = studyGroupService.findJoinedGroups(tokenUserId);

        // 간단 DTO로 변환 (프론트에서 쓰기 쉽게)
        var response = groups.stream()
                .map(g -> new java.util.HashMap<String, Object>() {{
                    put("groupId", g.getGroupId());
                    put("title", g.getTitle());
                    put("description", g.getDescription());
                    put("status", g.getStatus().name());
                    put("leaderId", g.getLeader().getUserId());
                    put("leaderName", g.getLeader().getName());
                }})
                .toList();

        return ResponseEntity.ok(response);
    }
}