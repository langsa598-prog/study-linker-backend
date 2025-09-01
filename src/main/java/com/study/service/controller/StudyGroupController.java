package com.study.service.controller;

import com.study.service.domain.studyGroup.StudyGroup;
import com.study.service.service.StudyGroupService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/study-groups")
public class StudyGroupController {
    private final StudyGroupService service;
    public StudyGroupController(StudyGroupService service) { this.service = service; }

    @GetMapping
    public List<StudyGroup> getAll() { return service.findAll(); }

    @PostMapping
    public StudyGroup create(@RequestBody StudyGroup group) { return service.save(group); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { service.deleteById(id); }
}
