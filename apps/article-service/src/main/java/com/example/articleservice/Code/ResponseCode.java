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
     * Police
     */
    SUCCESS_POLICE_REGISTER(HttpStatus.CREATED, "경찰 회원가입을 성공했습니다."),
    SUCCESS_POLICE_LOGIN(HttpStatus.OK, "로그인을 성공했습니다. 헤더 토큰을 확인하세요."),
    SUCCESS_POLICE_RETRIEVE_USER(HttpStatus.OK, "경찰 유저 정보를 성공적으로 조회했습니다."),
    SUCCESS_POLICE_REISSUE(HttpStatus.OK, "토큰 재발급을 성공했습니다. 헤더 토큰을 확인하세요."),
    SUCCESS_UPDATE_POLICE(HttpStatus.OK, "경찰 유저 정보를 성공적으로 수정했습니다."),
    SUCCESS_RETRIEVE_ALL_POLICES(HttpStatus.OK, "모든 경찰 사용자를 성공적으로 조회했습니다."),
    SUCCESS_POLICE_LOGOUT(HttpStatus.OK, "성공적으로 로그아웃했습니다."),
    SUCCESS_DELETE_POLICE(HttpStatus.OK, "경찰 유저가 성공적으로 삭제되었습니다."),
    SUCCESS_EMAIL_POLICE_SEND(HttpStatus.OK, "경찰 이메일 인증번호를 전송했습니다."),
    SUCCESS_EMAIL_POLICE_ACCEPT(HttpStatus.OK, "경찰 이메일 인증을 성공했습니다."),


    /**
     * User
     */
    SUCCESS_REGISTER(HttpStatus.CREATED, "회원가입을 성공했습니다."),
    SUCCESS_LOGIN(HttpStatus.OK, "로그인을 성공했습니다. 헤더 토큰을 확인하세요."),
    SUCCESS_RETRIEVE_USER(HttpStatus.OK, "유저 정보를 성공적으로 조회했습니다."),
    SUCCESS_REISSUE(HttpStatus.OK, "토큰 재발급을 성공했습니다. 헤더 토큰을 확인하세요."),
    SUCCESS_UPDATE_USER(HttpStatus.OK, "유저 정보를 성공적으로 수정했습니다."),
    SUCCESS_RETRIEVE_ALL_USERS(HttpStatus.OK, "모든 사용자를 성공적으로 조회했습니다."),
    SUCCESS_LOGOUT(HttpStatus.OK, "성공적으로 로그아웃했습니다."),
    SUCCESS_DELETE_USER(HttpStatus.OK, "유저가 성공적으로 삭제되었습니다."),
    SUCCESS_EMAIL_USER_SEND(HttpStatus.OK, "유저 이메일 인증번호를 전송했습니다."),
    SUCCESS_EMAIL_USER_ACCEPT(HttpStatus.OK, "유저 이메일 인증을 성공했습니다."),

    /**
     * Episode, Comment
     */

    SUCCESS_CREATE_POST(HttpStatus.OK, "게시글을 성공적으로 등록했습니다."),
    SUCCESS_CREATE_COMMENT(HttpStatus.OK, "댓글을 성공적으로 등록했습니다."),

    SUCCESS_POST_DELETE(HttpStatus.OK, "게시글을 성공적으로 삭제했습니다."),
    SUCCESS_COMMENT_DELETE(HttpStatus.OK, "댓글을 성공적으로 삭제했습니다."),

    SUCCESS_GET_POST(HttpStatus.OK, "게시글을 성공적으로 조회했습니다."),
    SUCCESS_GET_COMMENT(HttpStatus.OK, "댓글을 성공적으로 조회했습니다."),

    SUCCESS_UPDATE_POST(HttpStatus.OK, "게시글을 성공적으로 수정했습니다."),
    SUCCESS_UPDATE_COMMENT(HttpStatus.OK, "댓글을 성공적으로 수정했습니다."),

    /**
     * Like
     */

    SUCCESS_LIKE(HttpStatus.OK, "공감 완료되었습니다."),
    SUCCESS_LIKE_COUNT(HttpStatus.OK, "공감 수를 확인했습니다."),
    SUCCESS_CANCEL_LIKE(HttpStatus.OK, "게시글 공감을 취소했습니다.");

    private final HttpStatus status;
    private final String message;

}
