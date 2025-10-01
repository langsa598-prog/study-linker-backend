package com.study.service.studygroup.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.service.studygroup.domain.StudyGroup;
import com.study.service.studygroup.service.StudyGroupService;
import com.study.service.studygroup.dto.RecommendedGroupDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/study-groups")
public class StudyGroupController {

    private final StudyGroupService service;
    private final ObjectMapper objectMapper; // JSON 문자열 변환용

    public StudyGroupController(StudyGroupService service, ObjectMapper objectMapper) {
        this.service = service;
        this.objectMapper = objectMapper;
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

    // 추천 그룹 조회 (위치 + 관심 태그)
    @GetMapping("/recommend")
    public ResponseEntity<List<RecommendedGroupDto>> getRecommendedGroups(
            @RequestParam Double userLat,
            @RequestParam Double userLon,
            @RequestParam String interestTags
    ) {
        try {
            List<String> tagsList = Arrays.stream(interestTags.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());

            List<RecommendedGroupDto> recommendedGroups =
                    service.findRecommendedGroups(userLat, userLon, tagsList, 5.0);

            return ResponseEntity.ok(recommendedGroups);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}