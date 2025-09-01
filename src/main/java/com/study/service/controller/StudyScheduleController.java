package com.study.service.controller;

import com.study.service.domain.studySchedule.StudySchedule;
import com.study.service.service.StudyScheduleService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/study-schedules")
public class StudyScheduleController {
    private final StudyScheduleService service;
    public StudyScheduleController(StudyScheduleService service) { this.service = service; }

    @GetMapping
    public List<StudySchedule> getAll() { return service.findAll(); }

    @PostMapping
    public StudySchedule create(@RequestBody StudySchedule schedule) { return service.save(schedule); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { service.deleteById(id); }
}
