package com.study.service.groupmember.repository;

import com.study.service.groupmember.domain.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    // 특정 그룹 + 특정 유저 단건 조회 (중복 확인용)
    Optional<GroupMember> findByGroup_GroupIdAndUser_UserId(Long groupId, Long userId);

    // 특정 그룹의 모든 멤버 조회
    List<GroupMember> findByGroup_GroupId(Long groupId);
}
