package com.study.service.groupmember.dto;

import com.study.service.groupmember.domain.GroupMember;
import java.time.LocalDateTime;

public class GroupMemberResponse {

    private Long memberId;
    private Long groupId;
    private Long userId;
    private String username;   // ⭐ 추가됨 (Users.username)
    private String name;       // ⭐ 추가됨 (Users.name)
    private String role;
    private String status;
    private LocalDateTime joinedAt;

    public GroupMemberResponse(Long memberId, Long groupId, Long userId,
                               String username, String name,
                               String role, String status,
                               LocalDateTime joinedAt) {
        this.memberId = memberId;
        this.groupId = groupId;
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.role = role;
        this.status = status;
        this.joinedAt = joinedAt;
    }

    public static GroupMemberResponse fromEntity(GroupMember member) {
        return new GroupMemberResponse(
                member.getMemberId(),
                member.getGroup().getGroupId(),
                member.getUser().getUserId(),
                member.getUser().getUsername(),  // ⭐ Users.username
                member.getUser().getName(),      // ⭐ Users.name
                member.getRole().name(),
                member.getStatus().name(),
                member.getJoinedAt()
        );
    }

    public Long getMemberId() { return memberId; }
    public Long getGroupId() { return groupId; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }  // ⭐ getter 추가
    public String getName() { return name; }          // ⭐ getter 추가
    public String getRole() { return role; }
    public String getStatus() { return status; }
    public LocalDateTime getJoinedAt() { return joinedAt; }
}