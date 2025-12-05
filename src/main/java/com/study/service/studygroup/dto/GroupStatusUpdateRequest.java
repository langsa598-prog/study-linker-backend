package com.study.service.studygroup.dto;

/**
 * PATCH /api/study-groups/{groupId}
 * {
 *   "status": "Active" | "Inactive" | "Pending" | "Rejected"
 * }
 */
public class GroupStatusUpdateRequest {

    private String status;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}