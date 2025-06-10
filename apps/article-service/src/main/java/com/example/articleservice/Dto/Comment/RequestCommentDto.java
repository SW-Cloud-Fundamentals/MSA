package com.example.articleservice.Dto.Comment;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequestCommentDto {
    private String content;
    private Long articleId;
}
