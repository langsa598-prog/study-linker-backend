package com.study.service.controller;

import com.study.service.domain.attendance.Attendance;
import com.study.service.service.AttendanceService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    private final AttendanceService service;
    public AttendanceController(AttendanceService service) { this.service = service; }

    @GetMapping
    public List<Attendance> getAll() { return service.findAll(); }

    @PostMapping
    public Attendance create(@RequestBody Attendance attendance) { return service.save(attendance); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { service.deleteById(id); }
}