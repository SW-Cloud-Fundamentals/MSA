package com.example.userservice.dto.email;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * “인증번호 확인” 요청 DTO
 */
@Getter
@Setter
public class EmailCheckDto {

    @NotBlank(message = "이메일을 입력해 주세요.")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@skuniv\\.ac\\.kr$",
            message = "@skuniv.ac.kr 주소만 사용할 수 있습니다."
    )
    private String email;

    @NotBlank(message = "인증번호를 입력해 주세요.")
    @Pattern(regexp = "\\d{6}", message = "인증번호는 6자리 숫자여야 합니다.")
    private String authNum;   // 사용자가 입력한 6자리 코드
}
