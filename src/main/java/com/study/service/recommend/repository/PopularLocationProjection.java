package com.study.service.recommend.repository;

public interface PopularLocationProjection {

    Long getGroupId();
    String getTitle();
    String getDescription();
    Long getMemberCount();
    Integer getMaxMembers();
    String getStatus();
    Double getLatitude();
    Double getLongitude();
    Double getDistanceKm();
}