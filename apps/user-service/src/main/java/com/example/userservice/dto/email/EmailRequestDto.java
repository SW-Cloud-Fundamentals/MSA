package com.example.userservice.dto.email;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * “인증번호 전송” 요청 DTO
 */
@Getter
@Setter
public class EmailRequestDto {

    @NotBlank(message = "이메일을 입력해 주세요.")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@skuniv\\.ac\\.kr$",
            message = "@skuniv.ac.kr 주소만 사용할 수 있습니다."
    )
    private String email;
}
