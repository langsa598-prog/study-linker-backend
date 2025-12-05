package com.study.service.studygroup.service;

import com.study.service.groupmember.domain.GroupMember;
import com.study.service.groupmember.dto.GroupMemberResponse;
import com.study.service.groupmember.repository.GroupMemberRepository;
import com.study.service.studyschedule.domain.StudySchedule;
import com.study.service.studyschedule.domain.StudyScheduleStatus;
import com.study.service.studyschedule.dto.StudyScheduleRequest;
import com.study.service.studyschedule.dto.StudyScheduleResponse;
import com.study.service.notification.service.NotificationService;
import com.study.service.notification.dto.NotificationRequest; // ✅ 이 부분이 추가되었습니다!
import com.study.service.studyschedule.repository.StudyScheduleRepository;
import com.study.service.studygroup.domain.GroupStatus;
import com.study.service.studygroup.domain.StudyGroup;
import com.study.service.studygroup.dto.StudyGroupRequest;
import com.study.service.studygroup.repository.StudyGroupRepository;
import com.study.service.user.domain.User;
import com.study.service.user.repository.UserRepository;
import com.study.service.user.service.MannerScoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class StudyGroupService {

    private final StudyGroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final StudyScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final MannerScoreService mannerScoreService;
    // ⭐ 알림을 사용하기 위해 NotificationService 의존성 추가
    // (알림을 보내려면 꼭 필요함)
    private final NotificationService notificationService;

    public StudyGroupService(
            StudyGroupRepository groupRepository,
            GroupMemberRepository memberRepository,
            StudyScheduleRepository scheduleRepository,
            UserRepository userRepository,
            MannerScoreService mannerScoreService,
            NotificationService notificationService
    ) {
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
        this.mannerScoreService = mannerScoreService;
        // ⭐ 알림 주입 (가입신청/승인/거절 알림 보내기 위함)
        this.notificationService = notificationService;
    }

    // ============================
    // 스터디 그룹 전체 조회
    // ============================
    public List<StudyGroup> findAll() {
        return groupRepository.findAll();
    }

    // ============================
    // 단건 조회
    // ============================
    public StudyGroup findById(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));
    }

    // ============================
    // 그룹 생성 (요청자가 리더)
    // ============================
    @Transactional
    public StudyGroup createGroup(StudyGroupRequest request, Long leaderId) {

        User leader = userRepository.findById(leaderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저 없음: " + leaderId));

        StudyGroup group = new StudyGroup();
        group.setLeader(leader);
        group.setTitle(request.getTitle());
        group.setDescription(request.getDescription());
        group.setMaxMembers(request.getMaxMembers());

        // category: JSON 문자열 기대, null/빈문자면 "[]"로 처리
        if (request.getCategory() == null || request.getCategory().isBlank()) {
            group.setCategory("[]");
        } else {
            group.setCategory(request.getCategory());
        }

        // Double -> BigDecimal 변환
        if (request.getLatitude() != null) {
            group.setLatitude(BigDecimal.valueOf(request.getLatitude()));
        } else {
            group.setLatitude(null);
        }

        if (request.getLongitude() != null) {
            group.setLongitude(BigDecimal.valueOf(request.getLongitude()));
        } else {
            group.setLongitude(null);
        }

        // status는 기본값 사용
        StudyGroup saved = groupRepository.save(group);

        // 리더를 GroupMember에도 자동 등록
        GroupMember leaderMember = new GroupMember();
        leaderMember.setGroup(saved);
        leaderMember.setUser(leader);
        leaderMember.setRole(GroupMember.Role.LEADER);
        leaderMember.setStatus(GroupMember.Status.APPROVED);
        memberRepository.save(leaderMember);

        // 리더 매너 점수 +3 (스터디 그룹 생성 시)
        mannerScoreService.updateMannerScore(leader.getUserId(), "leader_score", 3.0f);

        return saved;
    }

    // ============================
    // 그룹 수정 (리더 + 관리자)
    // ============================
    @Transactional
    public StudyGroup updateGroup(Long groupId,
                                  StudyGroupRequest request,
                                  Long requesterId,
                                  boolean isAdmin) {

        StudyGroup group = findById(groupId); // 없으면 IllegalArgumentException

        Long leaderId = group.getLeader().getUserId();

        // 🔥 리더도 아니고 관리자도 아니면 권한 없음
        if (!isAdmin && !leaderId.equals(requesterId)) {
            throw new SecurityException("해당 그룹의 리더 또는 관리자만 수정할 수 있습니다.");
        }

        // 필드 업데이트 (createGroup 과 동일한 기준 유지)
        group.setTitle(request.getTitle());
        group.setDescription(request.getDescription());
        group.setMaxMembers(request.getMaxMembers());

        // category: JSON 문자열 기대, null/빈문자면 "[]"로 처리
        if (request.getCategory() == null || request.getCategory().isBlank()) {
            group.setCategory("[]");
        } else {
            group.setCategory(request.getCategory());
        }

        // Double -> BigDecimal 변환
        if (request.getLatitude() != null) {
            group.setLatitude(BigDecimal.valueOf(request.getLatitude()));
        } else {
            group.setLatitude(null);
        }

        if (request.getLongitude() != null) {
            group.setLongitude(BigDecimal.valueOf(request.getLongitude()));
        } else {
            group.setLongitude(null);
        }

        // JPA 더티체킹으로도 되지만, 명시적으로 save 한 번 해줘도 OK
        return groupRepository.save(group);
    }

    // ============================
    // 그룹 삭제 (리더 + 관리자)
    // ============================
    @Transactional
    public void deleteById(Long groupId, Long requesterId, boolean isAdmin) {

        StudyGroup group = findById(groupId);

        // 🔥 리더도 아니고 관리자도 아니면 권한 없음
        if (!isAdmin && !group.getLeader().getUserId().equals(requesterId)) {
            throw new SecurityException("해당 그룹의 리더 또는 관리자만 삭제할 수 있습니다.");
        }

        groupRepository.delete(group);
    }

    // ============================
    // 그룹 상태 변경 (리더 + 관리자)
    // ============================
    @Transactional
    public void updateStatus(Long groupId, String newStatus, Long requesterId, boolean isAdmin) {

        StudyGroup group = findById(groupId);

        // 🔥 리더도 아니고 관리자도 아니면 권한 없음
        if (!isAdmin && !group.getLeader().getUserId().equals(requesterId)) {
            throw new SecurityException("해당 그룹의 리더 또는 관리자만 상태를 변경할 수 있습니다.");
        }

        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException("status 값은 비어 있을 수 없습니다.");
        }

        GroupStatus statusEnum;
        try {
            statusEnum = GroupStatus.valueOf(newStatus.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "유효하지 않은 그룹 상태입니다. 사용 가능 값: PENDING, ACTIVE, INACTIVE, REJECTED"
            );
        }

        group.setStatus(statusEnum);
    }


    // ============================
    // 멤버 목록 조회 (공용 메서드)
    // ============================
    public List<GroupMemberResponse> getGroupMembers(Long groupId) {
        return memberRepository.findByGroupGroupId(groupId)
                .stream()
                .map(GroupMemberResponse::fromEntity)
                .toList();
    }

    // ============================
    // 멤버 목록 조회 (리더 권한 검증 포함)
    // ============================
    @Transactional(readOnly = true)
    public List<GroupMemberResponse> getGroupMembersAsLeader(Long groupId, Long requesterId) {

        StudyGroup group = findById(groupId);

        if (!group.getLeader().getUserId().equals(requesterId)) {
            throw new SecurityException("해당 그룹의 리더만 멤버 목록을 조회할 수 있습니다.");
        }

        return getGroupMembers(groupId);
    }

    // ============================
    // 특정 멤버 조회
    // ============================
    public GroupMemberResponse getGroupMember(Long groupId, Long userId) {
        GroupMember member = memberRepository.findByGroupGroupIdAndUserUserId(groupId, userId)
                .orElseThrow(() -> new IllegalArgumentException("멤버가 존재하지 않습니다."));
        return GroupMemberResponse.fromEntity(member);
    }

    // ============================
    // 리더 조회
    // ============================
    public GroupMemberResponse getGroupLeader(Long groupId) {
        GroupMember leader = memberRepository.findByGroupGroupIdAndRole(groupId, GroupMember.Role.LEADER)
                .orElseThrow(() -> new IllegalArgumentException("리더가 존재하지 않습니다."));
        return GroupMemberResponse.fromEntity(leader);
    }

    // ============================
    // 가입 신청
    // ============================
    @Transactional
    public GroupMemberResponse requestJoinGroup(Long groupId, Long userId) {

        StudyGroup group = findById(groupId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 중복 신청 방지
        if (memberRepository.existsByGroupGroupIdAndUserUserId(groupId, userId)) {
            throw new IllegalArgumentException("이미 신청했거나 가입된 유저입니다.");
        }

        GroupMember member = new GroupMember();
        member.setGroup(group);
        member.setUser(user);
        member.setRole(GroupMember.Role.MEMBER);
        member.setStatus(GroupMember.Status.PENDING);

        GroupMemberResponse response = GroupMemberResponse.fromEntity(memberRepository.save(member));

        // ⭐ 가입 신청 알림: 리더에게 전송
        NotificationRequest req = new NotificationRequest(); // ✅ 객체 직접 생성
        req.setMessage(user.getUsername() + "님이 스터디 가입을 요청했습니다.");
        req.setType("REQUEST");
        notificationService.save(group.getLeader().getUserId(), req); // ✅ Object 대신 NotificationRequest 사용

        return response;
    }

    // ============================
    // 가입 승인
    // ============================
    @Transactional
    public void approveMember(Long groupId, Long targetUserId, Long leaderId) {

        StudyGroup group = findById(groupId);

        if (!group.getLeader().getUserId().equals(leaderId)) {
            throw new SecurityException("리더만 승인할 수 있습니다.");
        }

        GroupMember member = memberRepository
                .findByGroupGroupIdAndUserUserId(groupId, targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("멤버가 존재하지 않습니다."));

        member.setStatus(GroupMember.Status.APPROVED);

        // ⭐ 가입 승인 알림: 승인된 사용자에게 전송
        NotificationRequest req = new NotificationRequest(); // ✅ 객체 직접 생성
        req.setMessage("스터디 가입 요청이 승인되었습니다.");
        req.setType("REQUEST");
        notificationService.save(targetUserId, req); // ✅ Object 대신 NotificationRequest 사용
    }

    // ============================
    // 가입 거절
    // ============================
    @Transactional
    public void rejectMember(Long groupId, Long targetUserId, Long leaderId) {

        StudyGroup group = findById(groupId);

        if (!group.getLeader().getUserId().equals(leaderId)) {
            throw new SecurityException("리더만 거절할 수 있습니다.");
        }

        GroupMember member = memberRepository
                .findByGroupGroupIdAndUserUserId(groupId, targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("멤버가 존재하지 않습니다."));

        member.setStatus(GroupMember.Status.REJECTED);


        // ⭐ 가입 거절 알림: 대상 사용자에게 전송
        NotificationRequest req = new NotificationRequest(); // ✅ 객체 직접 생성
        req.setMessage("스터디 가입 요청이 거절되었습니다.");
        req.setType("REQUEST");
        notificationService.save(targetUserId, req); // ✅ Object 대신 NotificationRequest 사용
    }

    // ============================
    // 스케줄 목록 조회
    // ============================
    public List<StudyScheduleResponse> getGroupSchedules(Long groupId) {
        return scheduleRepository.findByGroupGroupId(groupId)
                .stream()
                .map(StudyScheduleResponse::fromEntity)
                .toList();
    }

    // ============================
    // 스케줄 생성 (리더만)
    // ============================
    @Transactional
    public StudyScheduleResponse createSchedule(Long groupId, Long leaderId, StudyScheduleRequest request) {

        StudyGroup group = findById(groupId);

        if (!group.getLeader().getUserId().equals(leaderId)) {
            throw new SecurityException("리더만 일정 등록이 가능합니다.");
        }

        StudySchedule schedule = new StudySchedule();
        schedule.setGroup(group);
        schedule.setTitle(request.getTitle());
        schedule.setDescription(request.getDescription());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setLocation(request.getLocation());
        schedule.setStatus(StudyScheduleStatus.SCHEDULED);

        StudySchedule saved = scheduleRepository.save(schedule);

        // 일정 생성 시 리더 매너 점수 +0.1
        mannerScoreService.updateMannerScore(leaderId, "leader_score", 0.1f);

        return StudyScheduleResponse.fromEntity(saved);
    }

    // ============================
    // ✅ 내가 참여(승인)한 스터디 그룹 목록
    // ============================
    public List<StudyGroup> findJoinedGroups(Long userId) {

        List<GroupMember> members =
                memberRepository.findByUser_UserIdAndStatus(
                        userId,
                        GroupMember.Status.APPROVED
                );

        return members.stream()
                .map(GroupMember::getGroup)  // GroupMember → StudyGroup
                .toList();
    }
}