package com.example.articleservice.Dto.Comment;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentMessage implements Serializable {
    private String type;
    private Long commentId;
    private Long articleId;
    private String content;
    private String username;
    private String userRole;

    public static CommentMessage converter(String type, ResponseCommentDto dto) {
        return CommentMessage.builder()
                .type(type)
                .commentId(dto.getId())
                .articleId(dto.getArticleId())
                .content(dto.getContent())
                .username(dto.getUsername())
                .userRole(dto.getRole())
                .build();
    }
}
