package com.study.service.studypost.domain;

import com.study.service.studypost.domain.BoardType;
import com.study.service.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "study_posts")
@Getter
@Setter
public class StudyPost {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    private String title;

    @Lob
    private String content;

    private String location;

    private int maxMembers;

    private LocalDateTime studyDate;

    @ManyToOne
    @JoinColumn(name = "leader_id")
    private User leader;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<StudyReview> reviews;

    @Enumerated(EnumType.STRING)
    private BoardType type; // FREE, STUDY, REVIEW

    private LocalDateTime createdAt = LocalDateTime.now();
}
