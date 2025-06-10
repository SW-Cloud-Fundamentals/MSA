package com.example.userservice.controller;

import com.example.userservice.code.ResponseCode;
import com.example.userservice.dto.email.EmailCheckDto;
import com.example.userservice.dto.email.EmailRequestDto;
import com.example.userservice.dto.email.GeneralEmailCheckDto;
import com.example.userservice.dto.email.GeneralEmailRequestDto;
import com.example.userservice.dto.response.ResponseDTO;
import com.example.userservice.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    /** 인증번호 전송 */
    @PostMapping("/signup/police/email")
    public ResponseEntity<ResponseDTO<?>> sendPolice(@RequestBody @Valid EmailRequestDto dto) {
        emailService.sendPoliceCode(dto.getEmail());
        return ResponseEntity
                .status(ResponseCode.SUCCESS_EMAIL_POLICE_SEND.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_EMAIL_POLICE_SEND, null));
    }

    /** 인증번호 확인 */
    @PostMapping("/signup/police/emailAuth")
    public ResponseEntity<ResponseDTO<?>> checkPolice(@RequestBody @Valid EmailCheckDto dto) {
        emailService.verifyCode(dto.getEmail(), dto.getAuthNum());
        return ResponseEntity
                .status(ResponseCode.SUCCESS_EMAIL_POLICE_ACCEPT.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_EMAIL_POLICE_ACCEPT, null));
    }

    @PostMapping("/signup/general/email")
    public ResponseEntity<ResponseDTO<?>> sendGeneral(@RequestBody @Valid GeneralEmailRequestDto dto) {
        emailService.sendGeneralCode(dto.getEmail());
        return ResponseEntity
                .status(ResponseCode.SUCCESS_EMAIL_USER_SEND.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_EMAIL_USER_SEND, null));
    }

    @PostMapping("/signup/general/emailAuth")
    public ResponseEntity<ResponseDTO<?>> checkGeneral(@RequestBody @Valid GeneralEmailCheckDto dto) {
        emailService.verifyCode(dto.getEmail(), dto.getAuthNum());
        return ResponseEntity
                .status(ResponseCode.SUCCESS_EMAIL_USER_ACCEPT.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_EMAIL_USER_ACCEPT, null));
    }
}
