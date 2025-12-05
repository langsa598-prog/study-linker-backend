package com.study.service.recommend.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TagRecommendResponse {

    private final RecommendCriteria criteria;     // TAG_LOCATION
    private final double radiusKm;               // 사용자가 요청한 반경
    private final int limit;                     // 최대 개수
    private final List<TagRecommendGroupDto> groups; // 추천 결과 리스트
}
