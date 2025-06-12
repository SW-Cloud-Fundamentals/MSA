package com.example.sentimentservcie.Code;

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
     * Episode, Comment
     */

    SUCCESS_GET_SENTIMENT(HttpStatus.OK, "감정을 성공적으로 조회했습니다.");

    private final HttpStatus status;
    private final String message;

}
