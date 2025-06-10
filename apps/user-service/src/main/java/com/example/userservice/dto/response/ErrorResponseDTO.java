package com.example.userservice.dto.response;

import com.example.userservice.code.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

@Data
public class ErrorResponseDTO {
    private int status; // 404 200 뭐 띄우는 ErrorCode에 적어두었던 열거형을 꺼내오면 됨.
    private String error;
    private String code; // 404 200 딱 코드만 전달
    private String message; // 메시지 어떤 오류인지 전달
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, String> errors;
    // 예시로 레시피를 등록하는데 제목은 . 만 적고, 레시피는 비워뒀다면?
    // 제목은 . 만 적을 수 없습니다. (예시로, 최소 10글자 이상 적어주어야 합니다.)
    // 레시피는 비어있을 수 없습니다.

    // 라고 응답을 보내기 위한 매핑임.
    // entity 필드를 매핑해서 어떤 필드에서 오류가 났는지 응답으로 전달함.
    // 그리고 오류를 일관성 있게 전달하기 위해 DTO 사용하는 것임.


    public ErrorResponseDTO(ErrorCode errorCode) {
        this.status = errorCode.getStatus().value();
        this.error = errorCode.getStatus().name();
        this.code = errorCode.name();
        this.message = errorCode.getMessage();
    }

    // 내가 따로 전달한 메시지가 있을 때는 이거
    public ErrorResponseDTO(ErrorCode errorCode, String message) {
        this.status = errorCode.getStatus().value();
        this.error = errorCode.getStatus().name();
        this.code = errorCode.name();
        this.message = message;
    }


    // 내가 필드마다
    public ErrorResponseDTO(ErrorCode errorCode, Map<String, String> errors) {
        this.status = errorCode.getStatus().value();
        this.error = errorCode.getStatus().name();
        this.code = errorCode.name();
        this.message = errorCode.getMessage();
        this.errors = errors;
    }
}