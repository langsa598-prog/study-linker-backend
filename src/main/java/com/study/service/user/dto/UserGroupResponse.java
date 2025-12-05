package com.study.service.user.dto;

public class UserGroupResponse {

    private Long groupId;
    private String name;
    private String description;

    public UserGroupResponse(Long groupId, String name, String description) {
        this.groupId = groupId;
        this.name = name;
        this.description = description;
    }

    // ===== Getter =====
    public Long getGroupId() { return groupId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
}