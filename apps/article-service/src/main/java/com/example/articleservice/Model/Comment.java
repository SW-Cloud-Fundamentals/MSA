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

    @Column(nullable = false)
    private String username;

    @Column
    private String role;

    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    private Article articleId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public static Comment fromRequestDto(RequestCommentDto requestDto, String username, String role, Article article) {
        return Comment.builder()
                .content(requestDto.getContent())
                .username(username)
                .role(role)
                .articleId(article)
                .build();
    }
}
