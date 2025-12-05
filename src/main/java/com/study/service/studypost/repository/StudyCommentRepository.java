package com.study.service.studypost.repository;

import com.study.service.studypost.domain.StudyComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyCommentRepository extends JpaRepository<StudyComment, Long> {

    List<StudyComment> findByPostIdOrderByCreatedAtAsc(Long postId);

    int deleteByPostIdAndCommentId(Long postId, Long commentId);
}