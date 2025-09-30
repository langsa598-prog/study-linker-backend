package com.study.service.repository;

import com.study.service.domain.board.StudyReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyReviewRepository extends JpaRepository<StudyReview, Long> {
    List<StudyReview> findByPost_PostId(Long postId);
}