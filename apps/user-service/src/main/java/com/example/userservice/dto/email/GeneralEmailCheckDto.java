package com.example.userservice.dto.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GeneralEmailCheckDto {

    @NotBlank(message = "이메일을 입력해 주세요.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "인증번호를 입력해 주세요.")
    @Pattern(regexp = "\\d{6}", message = "인증번호는 6자리 숫자여야 합니다.")
    private String authNum;   // 사용자가 입력한 6자리 코드
}
