package com.example.userservice.controller;

import com.example.userservice.code.ResponseCode;
import com.example.userservice.dto.response.ResponseDTO;
import com.example.userservice.dto.user.AllUserDto;
import com.example.userservice.dto.user.RegisterUserDto;
import com.example.userservice.dto.user.ResponseUserDto;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping("/police")
    public ResponseEntity<ResponseDTO<?>> registerPoliceUser(@Valid @RequestBody RegisterUserDto registerUserDto) {
        userService.registerPolice(registerUserDto);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_POLICE_REGISTER.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_POLICE_REGISTER, null));
    }

    @PostMapping("/general")
    public ResponseEntity<ResponseDTO<?>> registerGeneralUser(@Valid @RequestBody RegisterUserDto registerUserDto) {
        userService.registerGeneral(registerUserDto);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_REGISTER.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_REGISTER, null));
    }

    /**
     * 모든 회원 조회 (경찰/일반 분리)
     */
    @GetMapping
    public ResponseEntity<ResponseDTO<AllUserDto>> getAllUsers() {
        AllUserDto dto = userService.findAllGrouped();
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_RETRIEVE_USER, dto));
    }

    /**
     * 로그인된 현재 정보 불러오기
     */
    @GetMapping("/me")
    public ResponseEntity<ResponseDTO<?>> getMyUserInfo(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring("Bearer ".length());
        ResponseUserDto dto = userService.getUsernameAndNickname(token);  // 또는 user 객체 바로 전달
        return ResponseEntity
                .status(ResponseCode.SUCCESS_RETRIEVE_USER.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_RETRIEVE_USER, dto));
    }

    /**
     * id 기반 회원 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<?>> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_DELETE_USER, null));
    }
}
