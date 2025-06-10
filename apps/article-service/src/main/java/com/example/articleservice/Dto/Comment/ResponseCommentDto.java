package com.example.articleservice.Dto.Comment;

import com.example.articleservice.Model.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCommentDto {
    private Long id;
    private String content;
    private String username;
    private String role;
    private Long articleId;
    private LocalDateTime createdAt;

    public static ResponseCommentDto dto(Comment comment) {
        return ResponseCommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .username(comment.getUsername())
                .role(comment.getRole())
                .articleId(comment.getArticleId().getId())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}

