package com.study.service.groupmember.service;

import com.study.service.groupmember.domain.GroupMember;
import com.study.service.groupmember.dto.GroupMemberRequest;
import com.study.service.groupmember.dto.GroupMemberResponse;
import com.study.service.studygroup.domain.StudyGroup;
import com.study.service.user.domain.User;
import com.study.service.studygroup.repository.StudyGroupRepository;
import com.study.service.user.repository.UserRepository;
import com.study.service.groupmember.repository.GroupMemberRepository;
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

    // === 기존 findAll, findByGroupIdAndUserId, findByGroup, addMember 그대로 사용 ===

    // 상태 업데이트 (관리자만)
    @Transactional
    public GroupMemberResponse updateStatusAsAdmin(Long memberId,
                                                   String status,
                                                   boolean isAdmin) {

        if (!isAdmin) {
            throw new IllegalArgumentException("관리자만 멤버 상태를 변경할 수 있습니다.");
        }

        GroupMember member = repository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다. id=" + memberId));

        GroupMember.Status newStatus;
        try {
            newStatus = GroupMember.Status.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 상태 값입니다. status=" + status);
        }

        member.setStatus(newStatus);
        return GroupMemberResponse.fromEntity(member);
    }

    // 멤버 삭제 (관리자만)
    @Transactional
    public void deleteByIdAsAdmin(Long memberId, boolean isAdmin) {

        if (!isAdmin) {
            throw new IllegalArgumentException("관리자만 멤버를 삭제할 수 있습니다.");
        }

        GroupMember member = repository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다. id=" + memberId));

        repository.delete(member);
    }
}