package com.study.service.service;

import com.study.service.domain.groupMember.GroupMember;
import com.study.service.repository.GroupMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupMemberService {
    private final GroupMemberRepository repository;

    public GroupMemberService(GroupMemberRepository repository) {
        this.repository = repository;
    }

    public List<GroupMember> findAll() {
        return repository.findAll();
    }

    public Optional<GroupMember> findById(Long id) {
        return repository.findById(id);
    }

    public GroupMember save(GroupMember member) {
        return repository.save(member);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}