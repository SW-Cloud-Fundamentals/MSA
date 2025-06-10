package com.example.userservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AllUserDto {
    private final List<ResponseUserDto> police;
    private final List<ResponseUserDto> general;
}
