package com.example.userservice.dto.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegisterUserDto {
    @NotEmpty(message = "아이디는 비워둘 수 없습니다.")
    private String username;
    @NotEmpty(message = "비밀번호는 비워둘 수 없습니다.")
    private String password;
    @NotEmpty(message = "닉네임은 비워둘 수 없습니다.")
    private String nickname;
    @NotEmpty(message = "이름은 비워둘 수 없습니다.")
    private String name;
}
