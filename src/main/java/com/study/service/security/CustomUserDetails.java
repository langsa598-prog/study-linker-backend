package com.study.service.security;

import com.study.service.user.domain.Role;
import com.study.service.user.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Long userId;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Long userId, String username, String password, Role role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    public static CustomUserDetails from(User user) {
        return new CustomUserDetails(
                user.getUserId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole()
        );
    }

    public Long getUserId() {
        return userId;
    }

    // ✅ 관리자 여부 체크 메서드 추가
    public boolean isAdmin() {
        return authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // 필요시 만료/잠금 로직 추가 가능, 지금은 전부 true
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}