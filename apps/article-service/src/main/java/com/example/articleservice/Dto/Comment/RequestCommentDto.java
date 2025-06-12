package com.example.articleservice.Dto.Comment;

import lombok.Getter;
import lombok.Setter;

/** 댓글 작성, 수정할 때 입력받은 내용과 articleId 값에 관한 dto */
@Setter
@Getter
public class RequestCommentDto {
    private String content;
    private Long articleId;
}
