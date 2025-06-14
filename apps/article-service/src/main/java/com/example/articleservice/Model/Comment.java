package com.example.articleservice.Model;

import com.example.articleservice.Dto.Comment.RequestCommentDto;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "comments")
public class Comment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CREATE || UPDATE || DELETE
    @Column
    private String eventType;

    @Column(nullable = false)
    private String username;

    @Column
    private String nickname;

    @Column
    private String role;

    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    private Article articleId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private boolean deleted = false;

    /** INSERT 직전 → 두 컬럼 모두 now() */
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /** UPDATE 직전 → updatedAt 만 now() */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static Comment fromRequestDto(RequestCommentDto requestDto, String username, String nickname, String role, Article article) {
        return Comment.builder()
                .content(requestDto.getContent())
                .username(username)
                .nickname(nickname)
                .role(role)
                .articleId(article)
                .build();
    }

    public void updateEvent(String eventType) {
        this.eventType = eventType;
    }
}
