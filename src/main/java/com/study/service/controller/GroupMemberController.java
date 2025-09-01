package com.study.service.controller;

import com.study.service.domain.groupMember.GroupMember;
import com.study.service.service.GroupMemberService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/group-members")
public class GroupMemberController {
    private final GroupMemberService service;
    public GroupMemberController(GroupMemberService service) { this.service = service; }

    @GetMapping
    public List<GroupMember> getAll() { return service.findAll(); }

    @PostMapping
    public GroupMember create(@RequestBody GroupMember member) { return service.save(member); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { service.deleteById(id); }
}