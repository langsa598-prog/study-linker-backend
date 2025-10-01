package com.study.service.studypost.repository;

import com.study.service.studypost.domain.StudyPost;
import com.study.service.studypost.domain.BoardType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyPostRepository extends JpaRepository<StudyPost, Long> {
    List<StudyPost> findByType(BoardType type);
}
