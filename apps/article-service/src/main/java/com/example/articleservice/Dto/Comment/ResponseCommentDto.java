package com.example.articleservice.Dto.Comment;

import com.example.articleservice.Model.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 댓글과 관련된 모든 api 호출 시에 반환 될 json dto */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCommentDto {
    private Long id;
    private String eventType;
    private String content;
    private String username;
    private String nickname;
    private String role;
    private Long articleId;
    private LocalDateTime createdAt;

    public static ResponseCommentDto dto(Comment comment) {
        return ResponseCommentDto.builder()
                .id(comment.getId())
                .eventType(comment.getEventType())
                .content(comment.getContent())
                .username(comment.getUsername())
                .nickname(comment.getNickname())
                .role(comment.getRole())
                .articleId(comment.getArticleId().getId())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}

