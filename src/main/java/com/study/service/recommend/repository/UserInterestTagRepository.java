package com.study.service.recommend.repository;

import com.study.service.recommend.domain.UserInterestTag;
import com.study.service.recommend.domain.UserInterestTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserInterestTagRepository
        extends JpaRepository<UserInterestTag, UserInterestTagId> {

    /**
     * 유저 한 명의 관심 태그 전체 조회
     */
    @Query("SELECT uit.tag FROM UserInterestTag uit WHERE uit.userId = :userId")
    List<String> findTagsByUserId(@Param("userId") Long userId);
}