// 추천 알고리즘(관심사 + 반경 2km) 결과 응답용 DTO

package com.study.service.studygroup.dto;

public class RecommendedGroupDto {
    private Long groupId;
    private String title;
    private String category;
    private Double latitude;
    private Double longitude;
    private Double distance; // km 단위

    public RecommendedGroupDto(Long groupId, String title, String category,
                               Double latitude, Double longitude, Double distance) {
        this.groupId = groupId;
        this.title = title;
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
    }

    public Long getGroupId() { return groupId; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public Double getDistance() { return distance; }
}
