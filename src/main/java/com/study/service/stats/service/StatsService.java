package com.study.service.stats.service;

import com.study.service.stats.dto.ChartResponse;
import com.study.service.stats.dto.StatsSummaryResponse;
import com.study.service.stats.repository.AttendanceStatsRepository;
import com.study.service.stats.repository.MemberRatioRepository;
import com.study.service.stats.repository.StudyStatsRepository;
import com.study.service.studygroup.domain.GroupStatus;
import com.study.service.studygroup.repository.StudyGroupRepository;
import com.study.service.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StudyStatsRepository studyRepo;
    private final MemberRatioRepository ratioRepo;
    private final AttendanceStatsRepository attendanceRepo;
    private final UserRepository userRepository;
    private final StudyGroupRepository studyGroupRepository;

    // 스터디 개설 수 (월별)
    public ChartResponse getStudyCount() {
        List<Object[]> rows = studyRepo.getMonthlyStudyCount();

        List<String> labels = rows.stream().map(r -> (String) r[0]).toList();
        List<Long> data = rows.stream().map(r -> ((Number) r[1]).longValue()).toList();

        return new ChartResponse(labels, data);
    }

    // 카테고리 비율
    public ChartResponse getMemberRatio() {
        List<Object[]> rows = ratioRepo.getCategoryRatio();

        List<String> labels = rows.stream().map(r -> (String) r[0]).toList();
        List<Long> data = rows.stream().map(r -> ((Number) r[1]).longValue()).toList();

        return new ChartResponse(labels, data);
    }

    // 출석률
    public ChartResponse getAttendanceStats() {
        List<Object[]> rows = attendanceRepo.getAttendanceRatio();

        List<String> labels = rows.stream().map(r -> (String) r[0]).toList();
        List<Long> data = rows.stream().map(r -> ((Number) r[1]).longValue()).toList();

        return new ChartResponse(labels, data);
    }

    // 📌 4) 요약 통계 (대시보드 상단)
    public StatsSummaryResponse getSummary() {

        long totalUsers = userRepository.count();
        long activeStudies = studyGroupRepository.countByStatus(GroupStatus.ACTIVE);
        long newSignupsToday = userRepository.countNewUsersToday();

        return new StatsSummaryResponse(
                totalUsers,
                activeStudies,
                newSignupsToday
        );
    }
}