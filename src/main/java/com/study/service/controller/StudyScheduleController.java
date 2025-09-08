package com.study.service.controller;

import com.study.service.domain.studySchedule.StudySchedule;
import com.study.service.domain.studySchedule.dto.StudyScheduleRequest;
import com.study.service.domain.studySchedule.dto.StudyScheduleResponse;
import com.study.service.service.StudyScheduleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/study-schedules")
public class StudyScheduleController {

    private final StudyScheduleService service;

    public StudyScheduleController(StudyScheduleService service) {
        this.service = service;
    }

    // 전체 조회
    @GetMapping
    public List<StudyScheduleResponse> getAll() {
        return service.findAll().stream()
                .map(StudyScheduleResponse::new)
                .collect(Collectors.toList());
    }

    // 단일 조회
    @GetMapping("/{id}")
    public StudyScheduleResponse getById(@PathVariable Long id) {
        StudySchedule schedule = service.findById(id);
        return new StudyScheduleResponse(schedule);
    }

    @PostMapping
    public StudyScheduleResponse create(@RequestBody StudyScheduleRequest request) {
        StudySchedule schedule = service.save(request);
        return new StudyScheduleResponse(schedule);
    }

    @PutMapping("/{id}")
    public StudyScheduleResponse update(@PathVariable Long id, @RequestBody StudyScheduleRequest request) {
        StudySchedule schedule = service.update(id, request);
        return new StudyScheduleResponse(schedule);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}