package com.example.articleservice.Code;

// 프론트에서 받는 응답을 일정하게 유지해주기 위해
// 간단히 우리만의 알아들을 수 있는 설명과 에러 코드를 전달합니다.
// 우리 프로젝트에 맞는 오류 메시지를 전달하게 됨.

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    /**
     * 400 BAD_REQUEST: 잘못된 요청
     */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    CONTENT_EMPTY(HttpStatus.BAD_REQUEST, "내용은 비워둘 수 없습니다."),
    COMMENT_CONTENT_OUTNUMBER(HttpStatus.BAD_REQUEST, "글자 수를 초과하였습니다."),

    /**
     * 403 FORBIDDEN: 권한 없음
     */
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    USER_NOT_OWNER(HttpStatus.FORBIDDEN, "댓글 작성자만 수정 또는 삭제할 수 있습니다."),

    /**
     * 404 NOT FOUND
     */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    NICKNAME_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 닉네임을 가진 사용자를 찾을 수 없습니다."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "공감하지 않은 게시글입니다"),
    ALREADY_LIKED(HttpStatus.NOT_FOUND, "이미 공감하였습니다."),
    /**
     * 500 INTERNAL SERVER ERROR: 서버 에러
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}