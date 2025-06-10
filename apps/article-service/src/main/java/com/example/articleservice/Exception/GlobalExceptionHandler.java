package com.example.articleservice.Exception;

import com.example.articleservice.Code.ErrorCode;
import com.example.articleservice.Dto.Response.ErrorResponseDTO;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 입력값 검증 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity
                .status(ErrorCode.BAD_REQUEST.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.BAD_REQUEST, errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity
                .status(ErrorCode.BAD_REQUEST.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.BAD_REQUEST, errors));
    }

    @ExceptionHandler(CodeExpiredException.class)
    protected ResponseEntity<ErrorResponseDTO> handleCodeExpiredException(final CodeExpiredException e) {
        return ResponseEntity
                .status(ErrorCode.CODE_EXPIRED.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.CODE_EXPIRED));
    }

    @ExceptionHandler(UnverifiedCodeException.class)
    protected ResponseEntity<ErrorResponseDTO> handleUnverifiedCodeException(final UnverifiedCodeException e) {
        return ResponseEntity
                .status(ErrorCode.UNVERIFIED_CODE.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.UNVERIFIED_CODE));
    }

    /**
     * 우리가 만든 CustomException 공통 처리
     */
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponseDTO> handleCustomException(final CustomException e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus().value())
                .body(new ErrorResponseDTO(e.getErrorCode()));
    }

    /**
     * 예상치 못한 서버 내부 에러 처리
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponseDTO> handleAllOtherExceptions(final Exception e) {
        log.error("Unexpected error: ", e);
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
