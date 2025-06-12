package com.example.articleservice.Code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 프론트에서 받는 응답을 일정하게 유지해주기 위해
// 간단히 우리만의 알아들을 수 있는 설명과 성공 코드를 전달합니다.
// 우리 프로젝트에 맞는 성공 메시지를 전달하게 됨.

@RequiredArgsConstructor
@Getter

public enum ResponseCode {
    /**
     * Comment
     */

    SUCCESS_CREATE_COMMENT(HttpStatus.OK, "댓글을 성공적으로 등록했습니다."),

    SUCCESS_COMMENT_DELETE(HttpStatus.OK, "댓글을 성공적으로 삭제했습니다."),

    SUCCESS_GET_ARTICLE(HttpStatus.OK, "기사를 성공적으로 조회했습니다."),
    SUCCESS_GET_COMMENT(HttpStatus.OK, "댓글을 성공적으로 조회했습니다."),

    SUCCESS_UPDATE_COMMENT(HttpStatus.OK, "댓글을 성공적으로 수정했습니다."),

    /**
     * Like
     */

    SUCCESS_LIKE(HttpStatus.OK, "공감이 완료 또는 취소 되었습니다."),
    SUCCESS_LIKE_COUNT(HttpStatus.OK, "공감 수를 확인했습니다.");

    private final HttpStatus status;
    private final String message;

}
