package com.study.service.studyschedule;

import com.study.service.studyschedule.domain.StudySchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyScheduleRepository extends JpaRepository<StudySchedule, Long> {}