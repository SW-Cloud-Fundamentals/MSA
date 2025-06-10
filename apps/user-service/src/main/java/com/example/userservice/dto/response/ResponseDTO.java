package com.example.userservice.dto.response;

import com.example.userservice.code.ResponseCode;
import lombok.Data;

@Data
public class ResponseDTO<T> {
    private Integer status;
    private String code;
    private String message;
    private T data;

    // 응답을 일관성 있게 전달하기 위해 DTO 사용하는 것임.

    public ResponseDTO(ResponseCode responseCode, T data) {
        this.status = responseCode.getStatus().value();
        this.code = responseCode.name();
        this.message = responseCode.getMessage();
        this.data = data;
    }
}