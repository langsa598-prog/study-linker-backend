package com.study.service.studypost.dto;

import com.study.service.studypost.domain.StudyReview;

import java.time.LocalDateTime;

public class StudyReviewResponse {

    private Long reviewId;
    private Long postId;
    private Long userId;
    private String userName;  // users.name
    private int rating;
    private String content;
    private LocalDateTime createdAt;

    public static StudyReviewResponse fromEntity(StudyReview review) {
        StudyReviewResponse dto = new StudyReviewResponse();
        dto.reviewId = review.getReviewId();
        dto.postId = review.getPost().getPostId();
        dto.userId = review.getUser().getUserId();
        dto.rating = review.getRating();
        dto.content = review.getContent();
        dto.createdAt = review.getCreatedAt();

        if (review.getUser() != null) {
            dto.userName = review.getUser().getName(); // ✅ users.name 컬럼 기준
        }

        return dto;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public int getRating() {
        return rating;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
