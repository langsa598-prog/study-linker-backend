package com.study.service.recommend.service;

import com.study.service.recommend.dto.PopularLocationGroupDto;
import com.study.service.recommend.dto.PopularLocationResponse;
import com.study.service.recommend.dto.RecommendCriteria;
import com.study.service.recommend.repository.PopularLocationProjection;
import com.study.service.recommend.repository.PopularLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PopularLocationService {

    private final PopularLocationRepository popularLocationRepository;

    public PopularLocationResponse getPopularGroupsByLocation(
            double latitude,
            double longitude,
            Double radiusKmParam,
            Integer limitParam,
            Double popWeightParam,
            Double distanceWeightParam
    ) {
        // 기본값
        double radiusKm = (radiusKmParam == null ? 2.0 : radiusKmParam);
        int limit = (limitParam == null ? 10 : limitParam);

        double popWeight = (popWeightParam == null ? 0.7 : popWeightParam);
        double distanceWeight = (distanceWeightParam == null ? 0.3 : distanceWeightParam);

        // 가중치 정규화
        double sum = popWeight + distanceWeight;
        if (sum <= 0) {
            popWeight = 0.7;
            distanceWeight = 0.3;
        } else {
            popWeight /= sum;
            distanceWeight /= sum;
        }

        // 🔥 람다에서 사용할 final 사본
        final double finalPopWeight = popWeight;
        final double finalDistanceWeight = distanceWeight;

        // DB 조회 (반경 내 + 멤버 수 기준 정렬)
        List<PopularLocationProjection> projections =
                popularLocationRepository.findPopularGroupsByLocation(latitude, longitude, radiusKm, limit);

        if (projections.isEmpty()) {
            return new PopularLocationResponse(
                    RecommendCriteria.POPULARITY_LOCATION,
                    radiusKm,
                    limit,
                    List.of()
            );
        }

        // 최대 멤버 수 (popScore 정규화용)
        long maxMemberCount = projections.stream()
                .mapToLong(p -> p.getMemberCount() == null ? 0L : p.getMemberCount())
                .max()
                .orElse(1L);

        List<PopularLocationGroupDto> groups = projections.stream()
                .map(p -> {
                    long memberCount = p.getMemberCount() == null ? 0L : p.getMemberCount();
                    double distanceKm = p.getDistanceKm() == null ? 0.0 : p.getDistanceKm();

                    // 인기도 점수 (멤버 수 비율)
                    double popScore = maxMemberCount > 0
                            ? (double) memberCount / maxMemberCount
                            : 0.0;

                    // 거리 점수 (가까울수록 1, 반경 끝이면 0)
                    double distanceScore = 1.0 - (distanceKm / radiusKm);
                    if (distanceScore < 0) distanceScore = 0;

                    // 최종 점수 = 인기도 * 가중치 + 거리 * 가중치
                    double finalScore = finalPopWeight * popScore + finalDistanceWeight * distanceScore;

                    return new PopularLocationGroupDto(
                            p.getGroupId(),
                            p.getTitle(),
                            p.getDescription(),
                            memberCount,
                            p.getMaxMembers(),
                            p.getStatus(),
                            p.getLatitude(),
                            p.getLongitude(),
                            round(distanceKm),
                            round(popScore),
                            round(distanceScore),
                            round(finalScore)
                    );
                })
                .sorted(Comparator.comparing(PopularLocationGroupDto::getFinalScore).reversed())
                .toList();

        return new PopularLocationResponse(
                RecommendCriteria.POPULARITY_LOCATION,
                radiusKm,
                limit,
                groups
        );
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}