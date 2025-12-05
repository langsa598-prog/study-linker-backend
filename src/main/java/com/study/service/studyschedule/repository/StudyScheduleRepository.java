package com.study.service.studyschedule.repository;

import com.study.service.studyschedule.domain.StudySchedule;
import com.study.service.studyschedule.dto.MyScheduleResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;


public interface StudyScheduleRepository extends JpaRepository<StudySchedule, Long> {

    // 오늘 일정
    List<StudySchedule> findByStartTimeBetweenOrderByStartTimeAsc(LocalDateTime start, LocalDateTime end);

    // 다가올 일정 (현재 이후)
    List<StudySchedule> findByStartTimeAfterOrderByStartTimeAsc(LocalDateTime now);

    // 특정 그룹의 모든 일정
    List<StudySchedule> findByGroupGroupId(Long groupId);

    // ✅ "내 일정" : Study_schedules.user_id 기준으로 조회
    @Query(
            value = """
        SELECT
            s.schedule_id,
            s.title,
            s.start_time,
            s.end_time,
            s.location,
            s.group_id
        FROM Study_schedules s
        WHERE 
            s.user_id = :userId
            OR s.group_id IN (
                SELECT gm.group_id
                FROM Group_members gm
                WHERE gm.user_id = :userId
                AND gm.status = 'APPROVED'
            )
        ORDER BY s.start_time DESC
        """,
            nativeQuery = true
    )
    List<Object[]> findRawMySchedules(Long userId);



    default List<MyScheduleResponse> getMySchedules(Long userId) {
        System.out.println(">>> [DEBUG] REPO: raw 쿼리 실행 userId=" + userId);
        List<Object[]> rows = findRawMySchedules(userId);

        System.out.println(">>> [DEBUG] REPO: raw rows size=" + rows.size());

        return rows.stream().map(r -> {
            System.out.println(">>> [DEBUG] 변환 row=" + Arrays.toString(r));

            Long gid = (r[5] == null ? null : ((Number) r[5]).longValue());

            return new MyScheduleResponse(
                    ((Number) r[0]).longValue(),
                    (String) r[1],
                    (Timestamp) r[2],
                    (Timestamp) r[3],
                    (String) r[4],
                    gid
            );
        }).toList();
    }

}