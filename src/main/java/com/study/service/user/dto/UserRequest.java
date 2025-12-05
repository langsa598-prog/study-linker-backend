package com.study.service.user.dto;

import java.util.List;

public class UserRequest {

    private String username;
    private String password;
    private String name;
    private String email;
    private List<String> interestTags;
    private Double latitude;
    private Double longitude;
    private String role;  // ✅ 추가: "USER", "ADMIN"

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getInterestTags() {
        return interestTags;
    }
    public void setInterestTags(List<String> interestTags) {
        this.interestTags = interestTags;
    }

    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    // ✅ 여기 추가
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
}