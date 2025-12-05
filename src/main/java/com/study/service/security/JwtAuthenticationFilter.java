package com.study.service.security;

import com.study.service.config.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
                                   CustomUserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 요청 URI 및 Authorization 헤더 확인
        System.out.println("--- [JWT FILTER] Request URI: " + request.getRequestURI() + " ---");
        String header = request.getHeader("Authorization");
        System.out.println(">>> Authorization header = " + header);

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            System.out.println(">>> token = " + token.substring(0, Math.min(token.length(), 30)) + "..."); // 토큰 일부만 출력

            // 2. 토큰 유효성 검사
            boolean valid = jwtTokenProvider.validateToken(token);
            System.out.println(">>> jwt validateToken result = " + valid);

            // 3. 토큰 유효하고 Security Context가 비어 있을 경우
            if (valid && SecurityContextHolder.getContext().getAuthentication() == null) {

                String username = jwtTokenProvider.getUsername(token);
                System.out.println(">>> jwt username from token = " + username);

                UserDetails userDetails = null;
                try {
                    userDetails = userDetailsService.loadUserByUsername(username);
                } catch (Exception e) {
                    System.err.println(">>> [ERROR] UserDetails 로드 실패: " + e.getMessage());
                    filterChain.doFilter(request, response);
                    return; // 로드 실패 시 Security Context 설정하지 않고 종료
                }

                System.out.println(">>> loaded userDetails class = " + userDetails.getClass().getName());

                // JWT 인증에서는 권한을 전달하는 생성자를 사용하여 인증(Authenticated) 상태를 true로 만듭니다.
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null, // credentials는 JWT 인증에서는 null
                                userDetails.getAuthorities() // 권한 목록
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);

                // 🌟 최종 수정: 인증 완료 상태와 권한을 명확하게 로그로 출력
                System.out.println(">>> SecurityContext setAuthentication 완료 (인증 상태: " + SecurityContextHolder.getContext().getAuthentication().isAuthenticated() + ")");
                System.out.println(">>> 인증된 권한: " + userDetails.getAuthorities());

            } else {
                // 4. 유효성 검사 실패 또는 이미 인증된 경우의 상세 로그
                if (!valid) {
                    System.out.println(">>> [FATAL] 토큰 검증 실패! (만료 또는 위변조가 유력한 원인)");
                } else if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    System.out.println(">>> [WARNING] 이미 Authentication 이 존재하여 스킵함 (인증 상태): " + SecurityContextHolder.getContext().getAuthentication().getName());
                } else {
                    System.out.println(">>> 토큰이 유효하지 않거나, 이미 Authentication 이 존재함");
                }
            }
        } else {
            // 5. 헤더 누락 또는 형식 오류
            System.out.println(">>> Authorization 헤더 없음 또는 Bearer 로 시작 안 함. (익명 요청)");
        }

        // 6. 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}