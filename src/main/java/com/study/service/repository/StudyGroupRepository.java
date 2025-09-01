package com.study.service.repository;

import com.study.service.domain.studyGroup.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {}
