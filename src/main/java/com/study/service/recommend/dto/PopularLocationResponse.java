package com.study.service.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PopularLocationResponse {

    // 어떤 기준의 추천인지 (여기서는 인기+위치)
    private RecommendCriteria criteria;

    // 프론트에서 요청한/적용된 반경 (km)
    private double radiusKm;

    // 최대 몇 개까지 응답했는지
    private int limit;

    // 실제 추천된 스터디 그룹 리스트
    private List<PopularLocationGroupDto> groups;
}