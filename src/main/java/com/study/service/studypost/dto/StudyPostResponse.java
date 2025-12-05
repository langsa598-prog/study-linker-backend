package com.study.service.studypost.dto;

import com.study.service.studypost.domain.BoardType;
import com.study.service.studypost.domain.StudyPost;
import java.time.LocalDateTime;

public class StudyPostResponse {

    private Long postId;
    private String title;
    private String content;
    private String location;
    private int maxMembers;
    private int currentMembers;
    private LocalDateTime studyDate;
    private Long leaderId;
    private String leaderName;
    private Long groupId;
    private Double latitude;
    private Double longitude;
    private String type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ✅ 신고 정보
    private Boolean reported;
    private String reportReason;

    // ✅ 엔티티 → DTO 변환 메서드
    public static StudyPostResponse fromEntity(StudyPost post) {
        StudyPostResponse dto = new StudyPostResponse();
        dto.postId = post.getPostId();
        dto.title = post.getTitle();
        dto.content = post.getContent();
        dto.location = post.getLocation();
        dto.maxMembers = post.getMaxMembers();
        dto.currentMembers = post.getCurrentMembers();
        dto.studyDate = post.getStudyDate();
        dto.createdAt = post.getCreatedAt();
        dto.updatedAt = post.getUpdatedAt();

        if (post.getLeader() != null) {
            dto.leaderId = post.getLeader().getUserId();
            dto.leaderName = post.getLeader().getName();
        }

        if (post.getGroup() != null) {
            dto.groupId = post.getGroup().getGroupId();
        }

        dto.latitude = post.getLatitude();
        dto.longitude = post.getLongitude();

        BoardType type = post.getType();
        dto.type = (type != null) ? type.name() : null;

        // ✅ 신고 정보 매핑
        dto.reported = post.getReported();
        dto.reportReason = post.getReportReason();

        return dto;
    }

    // --- getter 들 ---
    public Long getPostId() { return postId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getLocation() { return location; }
    public int getMaxMembers() { return maxMembers; }
    public int getCurrentMembers() { return currentMembers; }
    public LocalDateTime getStudyDate() { return studyDate; }
    public Long getLeaderId() { return leaderId; }
    public String getLeaderName() { return leaderName; }
    public Long getGroupId() { return groupId; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public String getType() { return type; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public Boolean getReported() { return reported; }
    public String getReportReason() { return reportReason; }
}