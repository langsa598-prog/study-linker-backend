package com.study.service.studypost.controller;

import com.study.service.security.CustomUserDetails;
import com.study.service.studypost.dto.*;
import com.study.service.studypost.service.StudyPostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/study-posts")
public class StudyPostController {

    private final StudyPostService studyPostService;

    public StudyPostController(StudyPostService studyPostService) {
        this.studyPostService = studyPostService;
    }

    // ==================== 유틸 메서드 ====================

    private boolean isLoggedIn(CustomUserDetails user) {
        return user != null;
    }

    private boolean isAdmin(CustomUserDetails user) {
        return user != null && user.isAdmin();
    }

    // ==================== 게시글 API ====================

    // GET /api/study-posts - 게시글 전체 목록 조회 (로그인만 되어 있으면 누구나)
    @GetMapping
    public ResponseEntity<?> getAllPosts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (!isLoggedIn(userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }
        List<StudyPostResponse> list = studyPostService.getAllPosts();
        return ResponseEntity.ok(list);
    }

    // GET /api/study-posts/{postId} - 게시글 상세 조회 (로그인 사용자라면 모두)
    @GetMapping("/{postId}")
    public ResponseEntity<?> getPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (!isLoggedIn(userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        StudyPostResponse post = studyPostService.getPost(postId);
        return ResponseEntity.ok(post);
    }

    // POST /api/study-posts - 게시글 생성
    //  → StudyPostService 안에서 "해당 스터디의 리더 또는 관리자" 인지 체크하도록 설계
    @PostMapping
    public ResponseEntity<?> createPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody StudyPostCreateRequest request
    ) {
        if (!isLoggedIn(userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        Long userId = userDetails.getUserId();
        boolean admin = isAdmin(userDetails);
        StudyPostResponse created = studyPostService.createPost(request, userId, admin);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PATCH /api/study-posts/{postId} - 게시글 수정 (작성자 + 리더 + 관리자 허용)
    @PatchMapping("/{postId}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody StudyPostUpdateRequest request
    ) {
        if (!isLoggedIn(userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        Long userId = userDetails.getUserId();
        boolean admin = isAdmin(userDetails);

        StudyPostResponse updated =
                studyPostService.updatePost(postId, request, userId, admin);

        return ResponseEntity.ok(updated);
    }

    // DELETE /api/study-posts/{postId}
    //  → 작성자 + 리더 + 관리자 허용
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (!isLoggedIn(userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        Long userId = userDetails.getUserId();
        boolean admin = isAdmin(userDetails);

        studyPostService.deletePost(postId, userId, admin);
        return ResponseEntity.noContent().build();
    }

    // ==================== 리뷰 API ====================

    @GetMapping("/{postId}/reviews")
    public ResponseEntity<?> getReviews(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (!isLoggedIn(userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        List<StudyReviewResponse> list = studyPostService.getReviewsByPost(postId);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/{postId}/reviews")
    public ResponseEntity<?> createReview(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody StudyReviewCreateRequest request
    ) {
        if (!isLoggedIn(userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        Long userId = userDetails.getUserId();
        StudyReviewResponse created =
                studyPostService.createReview(postId, userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{postId}/reviews/{reviewId}")
    public ResponseEntity<?> updateReview(
            @PathVariable Long postId,
            @PathVariable Long reviewId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody StudyReviewUpdateRequest request
    ) {
        if (!isLoggedIn(userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        Long userId = userDetails.getUserId();
        StudyReviewResponse updated =
                studyPostService.updateReview(postId, reviewId, userId, request);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{postId}/reviews/{reviewId}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long postId,
            @PathVariable Long reviewId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (!isLoggedIn(userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        Long userId = userDetails.getUserId();
        boolean admin = isAdmin(userDetails);

        studyPostService.deleteReview(postId, reviewId, userId, admin);
        return ResponseEntity.noContent().build();
    }

    // ==================== 댓글 API ====================

    @GetMapping("/{postId}/comments")
    public ResponseEntity<?> getComments(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (!isLoggedIn(userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        List<StudyCommentResponse> list =
                studyPostService.getCommentsByPost(postId);

        return ResponseEntity.ok(list);
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> createComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody StudyCommentRequest request
    ) {
        if (!isLoggedIn(userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        Long userId = userDetails.getUserId();
        StudyCommentCreate created =
                studyPostService.createComment(postId, userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (!isLoggedIn(userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        Long userId = userDetails.getUserId();
        boolean admin = isAdmin(userDetails);

        studyPostService.deleteComment(postId, commentId, userId, admin);
        return ResponseEntity.noContent().build();
    }
}