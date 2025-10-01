package com.study.service.user.dto;

import java.util.List;
import com.study.service.user.domain.Role;

public class UserResponse {
    private Long userId;
    private String username;
    private String email;
    private String name;
    private Role role;
    private List<String> interestTags;
    private Double latitude;
    private Double longitude;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public List<String> getInterestTags() { return interestTags; }
    public void setInterestTags(List<String> interestTags) { this.interestTags = interestTags; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}