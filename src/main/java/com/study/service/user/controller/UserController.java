package com.study.service.user.controller;

import com.study.service.user.domain.User;
import com.study.service.user.service.UserService;
import com.study.service.user.dto.UserRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // 전체 조회
    @GetMapping
    public List<User> getAll() {
        return service.findAll();
    }

    // 단일 조회
    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public User create(@RequestBody UserRequest request) {
        return service.save(request);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody UserRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}