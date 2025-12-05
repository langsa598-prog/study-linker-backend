package com.study.service.groupmember.repository;

import com.study.service.groupmember.domain.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    // 모든 멤버 (groupId 기준)
    List<GroupMember> findByGroupGroupId(Long groupId);

    // 특정 유저 (group + user 기준)
    Optional<GroupMember> findByGroupGroupIdAndUserUserId(Long groupId, Long userId);

    // 리더 조회
    Optional<GroupMember> findByGroupGroupIdAndRole(Long groupId, GroupMember.Role role);

    // 이미 신청/가입 했는지 체크
    boolean existsByGroupGroupIdAndUserUserId(Long groupId, Long userId);

    // 언더스코어 버전 (이미 서비스 코드에서 쓰고 있으면 그대로 유지)
    List<GroupMember> findByGroup_GroupId(Long groupId);

    Optional<GroupMember> findByGroup_GroupIdAndUser_UserId(Long groupId, Long userId);

    // ✅ 추가: 특정 유저가 "승인( APPROVED )된" 멤버로 참여 중인 그룹들
    //   → StudyGroupService에서 이걸로 가져와서 .getGroup() 으로 스터디 그룹 뽑을 거야
    List<GroupMember> findByUser_UserIdAndStatus(Long userId, GroupMember.Status status);
}