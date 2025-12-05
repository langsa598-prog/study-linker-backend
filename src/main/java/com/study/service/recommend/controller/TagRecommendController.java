package com.study.service.recommend.controller;

import com.study.service.recommend.dto.TagRecommendResponse;
import com.study.service.recommend.service.TagRecommendService;
import com.study.service.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class TagRecommendController {

    private final TagRecommendService tagRecommendService;

    @GetMapping("/tag")
    public TagRecommendResponse getTagBasedRecommend(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(required = false, defaultValue = "5") double radiusKm,
            @RequestParam(required = false, defaultValue = "10") int limit,
            @RequestParam(required = false, defaultValue = "0.5") double alpha,
            @RequestParam(required = false, defaultValue = "0.5") double beta
    ) {
        Long userId = userDetails.getUserId();
        return tagRecommendService.getTagBasedGroups(
                userId, lat, lng, radiusKm, limit, alpha, beta
        );
    }
}