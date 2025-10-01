package com.study.service.studygroup.repository;

import com.study.service.studygroup.domain.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {

    @Query(value = """
        SELECT
            sg.group_id,
            sg.title,
            sg.category,
            sg.latitude,
            sg.longitude,
            (6371 * acos(
                cos(radians(:userLat)) *
                cos(radians(sg.latitude)) *
                cos(radians(sg.longitude) - radians(:userLon)) +
                sin(radians(:userLat)) *
                sin(radians(sg.latitude))
            )) AS distance
        FROM study_groups sg
        WHERE JSON_CONTAINS(:interestTags, JSON_QUOTE(sg.category))
        HAVING distance <= :distanceKm
        ORDER BY distance ASC
        """, nativeQuery = true)
    List<Object[]> findRecommendedGroups(
            @Param("userLat") Double userLat,
            @Param("userLon") Double userLon,
            @Param("interestTags") String interestTags,
            @Param("distanceKm") Double distanceKm
    );
}