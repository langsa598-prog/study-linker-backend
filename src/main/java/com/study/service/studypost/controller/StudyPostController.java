package com.study.service.studypost.controller;

import com.study.service.studypost.domain.StudyReview;
import com.study.service.studypost.domain.BoardType;
import com.study.service.studypost.domain.StudyPost;
import com.study.service.user.domain.User;
import com.study.service.studypost.repository.StudyPostRepository;
import com.study.service.studypost.repository.StudyReviewRepository;
import com.study.service.user.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/study-posts")
public class StudyPostController {

    private final StudyPostRepository postRepository;
    private final StudyReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public StudyPostController(StudyPostRepository postRepository,
                               StudyReviewRepository reviewRepository,
                               UserRepository userRepository) {
        this.postRepository = postRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    //  게시글 전체 목록
    @GetMapping
    public List<StudyPost> list() {
        return postRepository.findAll();
    }

    @PostMapping
    public StudyPost create(@RequestBody StudyPost post,
                            @RequestParam Long leaderId) {
        User leader = userRepository.findById(leaderId).orElseThrow();
        post.setLeader(leader);

        if (post.getType() == null) {
            post.setType(BoardType.FREE);
        }

        return postRepository.save(post);
    }



    //  게시글 상세
    @GetMapping("/{id}")
    public StudyPost detail(@PathVariable Long id) {
        return postRepository.findById(id).orElseThrow();
    }

    //  리뷰 작성
    @PostMapping("/{id}/reviews")
    public StudyReview addReview(@PathVariable Long id,
                                 @RequestParam Long userId,
                                 @RequestBody StudyReview review) {
        StudyPost post = postRepository.findById(id).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        review.setPost(post);
        review.setUser(user);
        return reviewRepository.save(review);
    }

    //  특정 게시글의 리뷰 목록
    @GetMapping("/{id}/reviews")
    public List<StudyReview> reviews(@PathVariable Long id) {
        return reviewRepository.findByPost_PostId(id);
    }
}


