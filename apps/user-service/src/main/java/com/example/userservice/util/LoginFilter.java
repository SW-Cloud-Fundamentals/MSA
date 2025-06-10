package com.example.userservice.util;

import com.example.userservice.code.ErrorCode;
import com.example.userservice.code.ResponseCode;
import com.example.userservice.dto.response.ErrorResponseDTO;
import com.example.userservice.dto.response.ResponseDTO;
import com.example.userservice.dto.user.CustomUserDetails;
import com.example.userservice.model.User;
import com.example.userservice.model.UserRole;
import com.example.userservice.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String username = obtainUsername(request);
        String password = obtainPassword(request);

        // 추후 삭제 필요, 확인 용
        System.out.println(username + password);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        System.out.println(authToken);
        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = customUserDetails.getUsername();
        String nickname = customUserDetails.getNickname();
        UserRole role = customUserDetails.getRole();

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtil.createJwt("accessToken", username, nickname, role,86400000L);
        String refreshToken = jwtUtil.createJwt("refreshToken", username, nickname, role,86400000L);

        response.setHeader("accessToken", "Bearer " + accessToken);
        response.setHeader("refreshToken", "Bearer " + refreshToken);

        User user = userRepository.findByUsername(username).orElse(null);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(ResponseCode.SUCCESS_LOGIN, null);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(responseDTO);
        response.getWriter().write(jsonResponse);
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {

        response.setStatus(401);

        ErrorResponseDTO responseDTO = new ErrorResponseDTO(ErrorCode.USER_NOT_FOUND);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(responseDTO);
        response.getWriter().write(jsonResponse);
    }
}