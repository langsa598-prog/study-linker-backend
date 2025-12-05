package com.study.service.studypost.repository;

import com.study.service.studypost.domain.StudyPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyPostRepository extends JpaRepository<StudyPost, Long> {
}