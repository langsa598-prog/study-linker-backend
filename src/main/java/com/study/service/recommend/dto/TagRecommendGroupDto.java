package com.study.service.recommend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagRecommendGroupDto {

    private Long studyGroupId;
    private String name;

    // category JSON → ["Java","Spring"] 이런 리스트로 변환해서 넣어줄 예정
    private List<String> category;

    private Double latitude;
    private Double longitude;

    private double distanceKm;
    private double distanceScore;   // 1 / (1 + distanceKm)
    private double tagSimilarity;   // Jaccard
    private double finalScore;      // alpha * distanceScore + beta * tagSimilarity
}