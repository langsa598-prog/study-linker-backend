package com.study.service.studypost.repository;

import com.study.service.studypost.domain.StudyReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyReviewRepository extends JpaRepository<StudyReview, Long> {
    List<StudyReview> findByPost_PostId(Long postId);
}