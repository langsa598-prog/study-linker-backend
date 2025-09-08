package com.study.service.domain.groupMember.dto;

import com.study.service.domain.groupMember.GroupMember;
import java.time.LocalDateTime;

public class GroupMemberResponse {
    private Long memberId;
    private Long groupId;
    private Long userId;
    private String role;
    private String status;
    private LocalDateTime joinedAt;

    public GroupMemberResponse(Long memberId, Long groupId, Long userId,
                               String role, String status, LocalDateTime joinedAt) {
        this.memberId = memberId;
        this.groupId = groupId;
        this.userId = userId;
        this.role = role;
        this.status = status;
        this.joinedAt = joinedAt;
    }

    public static GroupMemberResponse fromEntity(GroupMember member) {
        return new GroupMemberResponse(
                member.getMemberId(),
                member.getGroup().getGroupId(),
                member.getUser().getUserId(),
                member.getRole().name(),
                member.getStatus().name(),
                member.getJoinedAt()
        );
    }

    public Long getMemberId() { return memberId; }
    public Long getGroupId() { return groupId; }
    public Long getUserId() { return userId; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
    public LocalDateTime getJoinedAt() { return joinedAt; }
}