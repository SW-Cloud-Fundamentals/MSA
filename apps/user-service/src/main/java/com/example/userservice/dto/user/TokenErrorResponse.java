package com.example.userservice.dto.user;

import com.example.userservice.code.ErrorCode;
import com.example.userservice.dto.response.ErrorResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.PrintWriter;

public class TokenErrorResponse {
    public static void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(errorCode);
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.setStatus(HttpStatus.BAD_REQUEST.value());

        PrintWriter writer = response.getWriter();
        writer.print(jsonResponse);
        writer.flush();
        writer.close();
    }
}
