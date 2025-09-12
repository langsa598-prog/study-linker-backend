package com.study.service.service;

import com.study.service.domain.studyGroup.StudyGroup;
import com.study.service.domain.studyGroup.dto.RecommendedGroupDto;
import com.study.service.domain.user.User;
import com.study.service.repository.StudyGroupRepository;
import com.study.service.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudyGroupService {

    private final StudyGroupRepository repository;
    private final UserRepository userRepository;

    public StudyGroupService(StudyGroupRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    // 모든 스터디 그룹 조회
    public List<StudyGroup> findAll() {
        return repository.findAll();
    }

    // 특정 스터디 그룹 조회
    public StudyGroup findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다. ID: " + id));
    }

    // 스터디 그룹 생성 (leader 자동 지정)
    public StudyGroup createGroup(StudyGroup group, Long userId) {
        User leader = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다. ID: " + userId));

        group.setLeader(leader);
        return repository.save(group);
    }

    // 스터디 그룹 삭제
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("삭제할 스터디 그룹이 존재하지 않습니다. ID: " + id);
        }
        repository.deleteById(id);
    }

    // 추천 그룹 조회 (Object[] → DTO 변환)
    public List<RecommendedGroupDto> findRecommendedGroups(Double userLat, Double userLon, String interestTags) {
        List<Object[]> results = repository.findRecommendedGroups(userLat, userLon, interestTags);

        return results.stream()
                .map(row -> new RecommendedGroupDto(
                        ((Number) row[0]).longValue(),   // groupId
                        (String) row[1],                 // title
                        (String) row[2],                 // category
                        ((Number) row[3]).doubleValue(), // latitude
                        ((Number) row[4]).doubleValue(), // longitude
                        ((Number) row[5]).doubleValue()  // distance
                ))
                .collect(Collectors.toList());
    }
}