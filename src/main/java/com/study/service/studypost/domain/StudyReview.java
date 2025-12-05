package com.study.service.studypost.domain;

import com.study.service.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Study_reviews")
@Getter
@Setter
public class StudyReview {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private StudyPost post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private int rating; // 1~5점
    @Lob
    private String content;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
