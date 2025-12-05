package com.study.service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Spring Security에서 CORS 설정을 담당하도록 WebMvcConfigurer의 CORS 설정을 주석 처리하거나 제거합니다.
        // registry.addMapping("/api/**")
        //         .allowedOriginPatterns(
        //                 "http://localhost:3000",          // 로컬 개발용 React
        //                 "http://gachon.studylink.click",  // 배포된 프론트
        //                 "https://gachon.studylink.click"  // HTTPS 대비
        //         )
        //         .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        //         .allowCredentials(true)
        //         .maxAge(3600);
    }
}