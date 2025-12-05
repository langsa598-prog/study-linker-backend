package com.study.service.recommend.repository;

public interface TagLocationProjection {

    Long getGroupId();
    String getTitle();
    String getCategory();   // JSON 문자열로 넘어옴
    Double getLatitude();
    Double getLongitude();
    Double getDistanceKm();
}