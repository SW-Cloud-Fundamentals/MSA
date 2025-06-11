package com.example.articleservice.Dto.Comment;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class CommentMessage implements Serializable {
    private Long id;
    private String eventType;
    private Long articleId;
    private String content;
    private String username;
    private String userRole;
}