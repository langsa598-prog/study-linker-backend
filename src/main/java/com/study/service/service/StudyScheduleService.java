package com.study.service.service;

import com.study.service.domain.studySchedule.StudySchedule;
import com.study.service.repository.StudyScheduleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudyScheduleService {
    private final StudyScheduleRepository repository;

    public StudyScheduleService(StudyScheduleRepository repository) {
        this.repository = repository;
    }

    public List<StudySchedule> findAll() {
        return repository.findAll();
    }

    public Optional<StudySchedule> findById(Long id) {
        return repository.findById(id);
    }

    public StudySchedule save(StudySchedule schedule) {
        return repository.save(schedule);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
