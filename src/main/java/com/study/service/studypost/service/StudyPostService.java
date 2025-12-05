package com.study.service.studypost.service;

import com.study.service.studypost.domain.BoardType;
import com.study.service.studypost.domain.StudyComment;
import com.study.service.studypost.domain.StudyPost;
import com.study.service.studypost.domain.StudyReview;
import com.study.service.studypost.dto.*;
import com.study.service.studypost.repository.StudyCommentRepository;
import com.study.service.studypost.repository.StudyPostRepository;
import com.study.service.studypost.repository.StudyReviewRepository;
import com.study.service.user.domain.User;
import com.study.service.user.repository.UserRepository;
import com.study.service.studygroup.domain.StudyGroup;
import com.study.service.studygroup.repository.StudyGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudyPostService {

    private final StudyPostRepository postRepository;
    private final StudyReviewRepository reviewRepository;
    private final StudyCommentRepository commentRepository;
    private final UserRepository userRepository;
    private final StudyGroupRepository studyGroupRepository;


    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StudyPostService(StudyPostRepository postRepository,
                            StudyReviewRepository reviewRepository,
                            StudyCommentRepository commentRepository,
                            UserRepository userRepository,
                            StudyGroupRepository studyGroupRepository) {
        this.postRepository = postRepository;
        this.reviewRepository = reviewRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.studyGroupRepository = studyGroupRepository;   // ⭐ 추가
    }


    // ===================== 게시글 =====================

    // 전체 조회
    @Transactional(readOnly = true)
    public List<StudyPostResponse> getAllPosts() {
        return postRepository.findAll().stream()
                .map(StudyPostResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 단건 조회
    @Transactional(readOnly = true)
    public StudyPostResponse getPost(Long postId) {
        StudyPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + postId));
        return StudyPostResponse.fromEntity(post);
    }

    // ===================== 게시글 생성 =====================
    @Transactional
    public StudyPostResponse createPost(StudyPostCreateRequest request,
                                        Long requesterId,
                                        boolean isAdmin) {

        // 로그인한 사용자
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. id=" + requesterId));

        // 리더 설정
        User leader;
        if (request.getLeaderId() == null) {
            leader = requester;
        } else {
            if (!isAdmin && !request.getLeaderId().equals(requesterId)) {
                throw new SecurityException("리더는 로그인한 사용자 본인만 설정할 수 있습니다.");
            }
            leader = userRepository.findById(request.getLeaderId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "리더 유저를 찾을 수 없습니다. id=" + request.getLeaderId()));
        }

        // ===============================
        // ⭐ 공지사항 전용 처리
        // ===============================
        if ("NOTICE".equalsIgnoreCase(request.getType())) {

            StudyPost notice = new StudyPost();
            notice.setLeader(leader);
            notice.setTitle(request.getTitle());
            notice.setContent(request.getContent());
            notice.setType(BoardType.NOTICE);

            // 공지는 필요 없는 값들을 기본값으로
            notice.setLocation("공지사항");
            notice.setMaxMembers(0);
            notice.setCurrentMembers(0);
            notice.setStudyDate(null);
            notice.setLatitude(null);
            notice.setLongitude(null);

            StudyPost saved = postRepository.save(notice);
            return StudyPostResponse.fromEntity(saved);
        }

        // ===============================
        // 📌 일반 STUDY / FREE / REVIEW 생성 처리
        // ===============================
        StudyPost post = new StudyPost();
        post.setLeader(leader);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setLocation(request.getLocation());

        post.setMaxMembers(request.getMaxMembers() != null ? request.getMaxMembers() : 0);
        post.setCurrentMembers(0);

        if (request.getStudyDate() != null) {
            post.setStudyDate(LocalDateTime.parse(request.getStudyDate(), formatter));
        }

        if (request.getType() != null) {
            post.setType(BoardType.valueOf(request.getType().toUpperCase()));
        }

        post.setLatitude(request.getLatitude());
        post.setLongitude(request.getLongitude());

        // ⭐ 여기에 추가!!
        if (request.getGroupId() != null) {
            StudyGroup group = studyGroupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> new IllegalArgumentException("스터디가 존재하지 않습니다. id=" + request.getGroupId()));
            post.setGroup(group);
        }

        // 저장
        StudyPost saved = postRepository.save(post);
        return StudyPostResponse.fromEntity(saved);
    }

    // ===================== 게시글 수정 =====================
    @Transactional
    public StudyPostResponse updatePost(Long postId,
                                        StudyPostUpdateRequest request,
                                        Long requesterId,
                                        boolean isAdmin) {

        StudyPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + postId));

        Long writerId = post.getLeader().getUserId();

        if (!writerId.equals(requesterId) && !isAdmin) {
            throw new SecurityException("게시글 작성자 또는 관리자만 수정할 수 있습니다.");
        }

        if (request.getTitle() != null) post.setTitle(request.getTitle());
        if (request.getContent() != null) post.setContent(request.getContent());
        if (request.getLocation() != null) post.setLocation(request.getLocation());
        if (request.getMaxMembers() != null) post.setMaxMembers(request.getMaxMembers());
        if (request.getStudyDate() != null)
            post.setStudyDate(LocalDateTime.parse(request.getStudyDate(), formatter));
        if (request.getType() != null)
            post.setType(BoardType.valueOf(request.getType().toUpperCase()));
        if (request.getCurrentMembers() != null)
            post.setCurrentMembers(request.getCurrentMembers());
        if (request.getLatitude() != null) post.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) post.setLongitude(request.getLongitude());

        // ⭐ 여기에 추가!!
        if (request.getGroupId() != null) {
            StudyGroup group = studyGroupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> new IllegalArgumentException("스터디가 존재하지 않습니다. id=" + request.getGroupId()));
            post.setGroup(group);
        }

        post.setUpdatedAt(LocalDateTime.now());
        return StudyPostResponse.fromEntity(post);
    }

    // ===================== 게시글 삭제 =====================
    @Transactional
    public void deletePost(Long postId, Long userId, boolean isAdmin) {
        StudyPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + postId));

        Long writerId = post.getLeader().getUserId();

        if (!writerId.equals(userId) && !isAdmin) {
            throw new SecurityException("게시글 작성자 또는 관리자만 삭제할 수 있습니다.");
        }

        postRepository.delete(post);
    }

    // ===================== 리뷰 =====================
    @Transactional(readOnly = true)
    public List<StudyReviewResponse> getReviewsByPost(Long postId) {
        return reviewRepository.findByPost_PostId(postId).stream()
                .map(StudyReviewResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public StudyReviewResponse createReview(Long postId,
                                            Long userId,
                                            StudyReviewCreateRequest request) {
        StudyPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + postId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. id=" + userId));

        StudyReview review = new StudyReview();
        review.setPost(post);
        review.setUser(user);
        review.setRating(request.getRating());
        review.setContent(request.getContent());

        StudyReview saved = reviewRepository.save(review);
        return StudyReviewResponse.fromEntity(saved);
    }

    @Transactional
    public StudyReviewResponse updateReview(Long postId,
                                            Long reviewId,
                                            Long userId,
                                            StudyReviewUpdateRequest request) {

        StudyReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. id=" + reviewId));

        if (!review.getPost().getPostId().equals(postId))
            throw new IllegalArgumentException("해당 게시글의 리뷰가 아닙니다.");

        if (!review.getUser().getUserId().equals(userId))
            throw new SecurityException("리뷰 작성자만 수정할 수 있습니다.");

        if (request.getContent() != null) review.setContent(request.getContent());
        if (request.getRating() != null) review.setRating(request.getRating());

        return StudyReviewResponse.fromEntity(review);
    }

    @Transactional
    public void deleteReview(Long postId,
                             Long reviewId,
                             Long userId,
                             boolean isAdmin) {

        StudyReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. id=" + reviewId));

        if (!review.getPost().getPostId().equals(postId))
            throw new IllegalArgumentException("해당 게시글의 리뷰가 아닙니다.");

        Long writerId = review.getUser().getUserId();

        if (!writerId.equals(userId) && !isAdmin) {
            throw new SecurityException("리뷰 작성자 또는 관리자만 삭제할 수 있습니다.");
        }

        reviewRepository.delete(review);
    }

    // ===================== 댓글 =====================
    @Transactional(readOnly = true)
    public List<StudyCommentResponse> getCommentsByPost(Long postId) {

        postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + postId));

        List<StudyComment> comments =
                commentRepository.findByPostIdOrderByCreatedAtAsc(postId);

        return comments.stream()
                .map(StudyCommentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public StudyCommentCreate createComment(Long postId,
                                            Long userId,
                                            StudyCommentRequest request) {

        StudyPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + postId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. id=" + userId));

        StudyComment comment = StudyComment.builder()
                .postId(post.getPostId())
                .userId(user.getUserId())
                .content(request.getContent())
                .build();

        StudyComment saved = commentRepository.save(comment);

        return StudyCommentCreate.builder()
                .message("댓글이 등록되었습니다.")
                .comment(StudyCommentResponse.fromEntity(saved))
                .build();
    }

    @Transactional
    public void deleteComment(Long postId,
                              Long commentId,
                              Long userId,
                              boolean isAdmin) {

        StudyComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다. id=" + commentId));

        if (!comment.getPostId().equals(postId))
            throw new IllegalArgumentException("해당 게시글의 댓글이 아닙니다.");

        Long writerId = comment.getUserId();

        if (!writerId.equals(userId) && !isAdmin) {
            throw new SecurityException("댓글 작성자 또는 관리자만 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
    }
}