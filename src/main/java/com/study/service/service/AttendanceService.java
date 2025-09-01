package com.study.service.service;

import com.study.service.domain.attendance.Attendance;
import com.study.service.repository.AttendanceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {
    private final AttendanceRepository repository;

    public AttendanceService(AttendanceRepository repository) {
        this.repository = repository;
    }

    public List<Attendance> findAll() {
        return repository.findAll();
    }

    public Optional<Attendance> findById(Long id) {
        return repository.findById(id);
    }

    public Attendance save(Attendance attendance) {
        return repository.save(attendance);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}