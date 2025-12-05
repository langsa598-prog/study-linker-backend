package com.study.service.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MannerScoreResponse {

    private Long userId;
    private Float currentMannerScore;
    private Float attendanceScore;
    private Float leaderScore;
    private Float violationScore;

    // 편의용 정적 팩토리 메서드
    public static MannerScoreResponse fromEntity(com.study.service.user.domain.User user) {
        return new MannerScoreResponse(
                user.getUserId(),
                user.getCurrentMannerScore(),
                user.getAttendanceScore(),
                user.getLeaderScore(),
                user.getViolationScore()
        );
    }
}
