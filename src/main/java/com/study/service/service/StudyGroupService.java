package com.study.service.service;

import com.study.service.domain.studyGroup.StudyGroup;
import com.study.service.repository.StudyGroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudyGroupService {
    private final StudyGroupRepository repository;

    public StudyGroupService(StudyGroupRepository repository) {
        this.repository = repository;
    }

    public List<StudyGroup> findAll() {
        return repository.findAll();
    }

    public Optional<StudyGroup> findById(Long id) {
        return repository.findById(id);
    }

    public StudyGroup save(StudyGroup group) {
        return repository.save(group);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}