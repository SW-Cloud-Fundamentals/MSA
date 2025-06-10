package com.example.userservice.dto.user;

import com.example.userservice.model.User;
import com.example.userservice.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ResponseUserDto {
    private Long id;
    private String username;
    private String nickname;
    private UserRole role;

    public static ResponseUserDto entityToDto(User user) {
        return ResponseUserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .build();
    }
}
