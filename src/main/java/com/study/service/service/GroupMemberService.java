package com.study.service.service;

import com.study.service.domain.groupMember.GroupMember;
import com.study.service.domain.groupMember.dto.GroupMemberRequest;
import com.study.service.domain.groupMember.dto.GroupMemberResponse;
import com.study.service.domain.studyGroup.StudyGroup;
import com.study.service.domain.user.User;
import com.study.service.repository.GroupMemberRepository;
import com.study.service.repository.StudyGroupRepository;
import com.study.service.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupMemberService {
    private final GroupMemberRepository repository;
    private final StudyGroupRepository groupRepository;
    private final UserRepository userRepository;

    public GroupMemberService(GroupMemberRepository repository,
                              StudyGroupRepository groupRepository,
                              UserRepository userRepository) {
        this.repository = repository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    // 전체 조회
    public List<GroupMemberResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(GroupMemberResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 단건 조회 (그룹+유저)
    public Optional<GroupMemberResponse> findByGroupIdAndUserId(Long groupId, Long userId) {
        return repository.findByGroup_GroupIdAndUser_UserId(groupId, userId)
                .map(GroupMemberResponse::fromEntity);
    }

    // 그룹별 조회
    public List<GroupMemberResponse> findByGroup(Long groupId) {
        return repository.findByGroup_GroupId(groupId)
                .stream()
                .map(GroupMemberResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 멤버 추가
    @Transactional
    public GroupMemberResponse addMember(GroupMemberRequest request) {
        StudyGroup group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("그룹을 찾을 수 없습니다"));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // 중복 체크
        repository.findByGroup_GroupIdAndUser_UserId(request.getGroupId(), request.getUserId())
                .ifPresent(existing -> {
                    throw new RuntimeException("이미 이 그룹에 가입된 유저입니다");
                });

        GroupMember member = new GroupMember();
        member.setGroup(group);
        member.setUser(user);

        if (request.getRole() != null) member.setRole(GroupMember.Role.valueOf(request.getRole()));
        if (request.getStatus() != null) member.setStatus(GroupMember.Status.valueOf(request.getStatus()));

        return GroupMemberResponse.fromEntity(repository.save(member));
    }

    // 상태 업데이트
    @Transactional
    public GroupMemberResponse updateStatus(Long memberId, String status) {
        GroupMember member = repository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다"));
        member.setStatus(GroupMember.Status.valueOf(status));
        return GroupMemberResponse.fromEntity(repository.save(member));
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}