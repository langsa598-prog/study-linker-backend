package com.study.service.studyschedule.service;

import com.study.service.studyschedule.domain.StudySchedule;
import com.study.service.studyschedule.domain.StudyScheduleStatus;
import com.study.service.studyschedule.dto.MyScheduleResponse;
import com.study.service.studyschedule.dto.StudyScheduleRequest;
import com.study.service.studyschedule.dto.StudyScheduleStatusUpdateRequest;
import com.study.service.studygroup.domain.StudyGroup;
import com.study.service.studygroup.repository.StudyGroupRepository;
import com.study.service.studyschedule.repository.StudyScheduleRepository;
import com.study.service.user.domain.User;
import com.study.service.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudyScheduleService {

    private final StudyScheduleRepository scheduleRepository;
    private final StudyGroupRepository groupRepository;
    private final UserRepository userRepository;

    public StudyScheduleService(StudyScheduleRepository scheduleRepository,
                                StudyGroupRepository groupRepository,
                                UserRepository userRepository) {
        this.scheduleRepository = scheduleRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    // 단건 조회
    public StudySchedule findById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "스터디 스케줄을 찾을 수 없습니다. ID: " + scheduleId
                ));
    }

    // ✅ 개인 일정 생성 (groupId는 있어도 되고 없어도 됨)
    @Transactional
    public StudySchedule save(StudyScheduleRequest request, Long ownerId) {
        StudySchedule schedule = new StudySchedule();

        // 개인 일정 주인
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + ownerId)
                );
        schedule.setUser(owner); // 엔티티의 user 필드 기준

        // 그룹 연결이 필요한 경우에만 groupId 사용
        if (request.getGroupId() != null) {
            StudyGroup group = groupRepository.findById(request.getGroupId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "스터디 그룹을 찾을 수 없습니다. ID: " + request.getGroupId()
                            ));
            schedule.setGroup(group);
        }

        schedule.setTitle(request.getTitle());
        schedule.setDescription(request.getDescription());
        schedule.setLocation(request.getLocation());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        // status는 Entity 기본값 SCHEDULED 사용

        return scheduleRepository.save(schedule);
    }

    // ✅ 일정 수정: 리더 or 개인 일정 주인
    @Transactional
    public StudySchedule update(Long scheduleId,
                                StudyScheduleRequest request,
                                Long loginUserId) {
        StudySchedule schedule = findById(scheduleId);

        boolean isOwner = schedule.getUser() != null
                && schedule.getUser().getUserId().equals(loginUserId);

        boolean isLeader = schedule.getGroup() != null
                && schedule.getGroup().getLeader() != null
                && schedule.getGroup().getLeader().getUserId().equals(loginUserId);

        if (!isOwner && !isLeader) {
            throw new SecurityException("일정 수정 권한이 없습니다.");
        }

        // group 변경은 리더일 때만 허용한다고 가정
        if (request.getGroupId() != null && isLeader) {
            StudyGroup group = groupRepository.findById(request.getGroupId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "스터디 그룹을 찾을 수 없습니다. ID: " + request.getGroupId()
                            ));
            schedule.setGroup(group);
        }

        schedule.setTitle(request.getTitle());
        schedule.setDescription(request.getDescription());
        schedule.setLocation(request.getLocation());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());

        return scheduleRepository.save(schedule);
    }

    // ✅ 일정 삭제: 리더 or 개인 일정 주인
    @Transactional
    public void deleteById(Long scheduleId, Long loginUserId) {
        StudySchedule schedule = findById(scheduleId);

        boolean isOwner = schedule.getUser() != null
                && schedule.getUser().getUserId().equals(loginUserId);

        boolean isLeader = schedule.getGroup() != null
                && schedule.getGroup().getLeader() != null
                && schedule.getGroup().getLeader().getUserId().equals(loginUserId);

        if (!isOwner && !isLeader) {
            throw new SecurityException("일정 삭제 권한이 없습니다.");
        }

        scheduleRepository.delete(schedule);
    }

    // ✅ 상태 변경: 리더만
    @Transactional
    public StudySchedule updateStatus(Long scheduleId,
                                      StudyScheduleStatusUpdateRequest request,
                                      Long loginUserId) {
        StudySchedule schedule = findById(scheduleId);

        if (schedule.getGroup() == null
                || schedule.getGroup().getLeader() == null
                || !schedule.getGroup().getLeader().getUserId().equals(loginUserId)) {
            throw new SecurityException("해당 스터디 그룹 리더만 일정 상태를 변경할 수 있습니다.");
        }

        String statusStr = request.getStatus();
        if (statusStr == null || statusStr.isBlank()) {
            throw new IllegalArgumentException("status 값이 비어 있습니다.");
        }

        try {
            StudyScheduleStatus newStatus =
                    StudyScheduleStatus.valueOf(statusStr.toUpperCase());
            schedule.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 상태 값입니다: " + statusStr);
        }

        return scheduleRepository.save(schedule);
    }

    // 📌 특정 유저의 일정 목록 조회
    public List<MyScheduleResponse> getMySchedules(Long userId) {
        System.out.println(">>> [DEBUG] 서비스: getMySchedules(userId=" + userId + ") 호출됨");

        List<MyScheduleResponse> list = scheduleRepository.getMySchedules(userId);

        System.out.println(">>> [DEBUG] 조회 결과 개수 = " + list.size());
        for (MyScheduleResponse r : list) {
            System.out.println(">>> [DEBUG] 일정 = id:" + r.getScheduleId()
                    + ", groupId:" + r.getGroupId());
        }

        return list;
    }

}