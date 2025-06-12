package com.example.articleservice.Dto.Comment;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * Kafka로 메시지를 전송할 때 사용할 dto.
 * CommentEntity와 필드명을 맞추기를 권장
 * */
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