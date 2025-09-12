package com.study.service.controller;

import com.study.service.domain.studyGroup.StudyGroup;
import com.study.service.domain.studyGroup.dto.RecommendedGroupDto;
import com.study.service.service.StudyGroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/study-groups")
public class StudyGroupController {

    private final StudyGroupService service;

    public StudyGroupController(StudyGroupService service) {
        this.service = service;
    }

    // 전체 조회
    @GetMapping
    public List<StudyGroup> getAll() {
        return service.findAll();
    }

    // 단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<StudyGroup> getById(@PathVariable Long id) {
        StudyGroup group = service.findById(id);
        return ResponseEntity.ok(group);
    }

    // 스터디 그룹 생성 (로그인한 사용자가 leader로 자동 지정)
    @PostMapping
    public ResponseEntity<StudyGroup> create(
            @RequestBody StudyGroup group,
            @RequestParam Long userId   // 로그인한 사용자 ID를 파라미터로 전달받음
    ) {
        StudyGroup created = service.createGroup(group, userId);
        return ResponseEntity.ok(created);
    }

    // 추천 그룹 조회
    @GetMapping("/recommend")
    public ResponseEntity<List<RecommendedGroupDto>> getRecommendedGroups(
            @RequestParam Double userLat,
            @RequestParam Double userLon,
            @RequestParam String interestTags
    ) {
        List<RecommendedGroupDto> recommendedGroups =
                service.findRecommendedGroups(userLat, userLon, interestTags);

        return ResponseEntity.ok(recommendedGroups);
    }
}