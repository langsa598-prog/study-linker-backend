package com.study.service.service;

import com.study.service.domain.studyGroup.StudyGroup;
import com.study.service.domain.user.User;
import com.study.service.repository.StudyGroupRepository;
import com.study.service.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

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

        group.setLeader(leader); // ✅ 로그인한 사용자를 리더로 설정
        return repository.save(group);
    }

    // 스터디 그룹 저장 (리더 지정 없이 단순 저장할 때 사용 가능)
    public StudyGroup save(StudyGroup group) {
        return repository.save(group);
    }

    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("삭제할 스터디 그룹이 존재하지 않습니다. ID: " + id);
        }
        repository.deleteById(id);
    }
}