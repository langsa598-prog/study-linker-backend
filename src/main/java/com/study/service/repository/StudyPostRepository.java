package com.study.service.repository;

import com.study.service.domain.board.BoardType;
import com.study.service.domain.board.StudyPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyPostRepository extends JpaRepository<StudyPost, Long> {
    List<StudyPost> findByType(BoardType type);
}
