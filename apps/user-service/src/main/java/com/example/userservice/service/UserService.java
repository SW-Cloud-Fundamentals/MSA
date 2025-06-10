package com.example.userservice.service;

import com.example.userservice.dto.user.AllUserDto;
import com.example.userservice.dto.user.RegisterUserDto;
import com.example.userservice.dto.user.ResponseUserDto;
import com.example.userservice.exception.DuplicateLoginIdException;
import com.example.userservice.exception.InvalidEmailDomainException;
import com.example.userservice.exception.UnauthenticatedEmailException;
import com.example.userservice.exception.UserNotExistException;
import com.example.userservice.model.EmailVerification;
import com.example.userservice.model.User;
import com.example.userservice.model.UserRole;
import com.example.userservice.repository.EmailVerificationRepository;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailVerificationRepository verifyRepo;
    private final JWTUtil jwtUtil;

    // 서경대 이메일 전용 회원 등록
    public void registerPolice(RegisterUserDto registerUserDto) {
        String username = registerUserDto.getUsername();

        if (!username.endsWith("@skuniv.ac.kr")) {
            throw new InvalidEmailDomainException("@skuniv.ac.kr 주소만 사용할 수 있습니다.");
        }

        // 1) 이메일 인증 여부 체크
        EmailVerification ev = verifyRepo.findByEmail(username)
                .orElseThrow(() -> new UnauthenticatedEmailException("이메일 인증을 먼저 해주세요."));
        // 코드 일치ㆍ만료 확인 → 통과 못 하면 예외 발생

        if (!ev.isVerified())
            throw new IllegalArgumentException("이메일 인증이 아직 완료되지 않았습니다.");

        // 2) 중복 ID 검사
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateLoginIdException("중복된 아이디로는 가입할 수 없습니다.");
        }

        // 3) 회원 저장
        User user = User.signupBuilder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(registerUserDto.getPassword()))
                .nickname(registerUserDto.getNickname())
                .name(registerUserDto.getName())
                .role(UserRole.POLICE)
                .build();

        userRepository.save(user);

        /* 4) 인증 기록 삭제 (한 번 소모) */
        verifyRepo.delete(ev);
    }

    public void registerGeneral(RegisterUserDto registerUserDto) {
        String username = registerUserDto.getUsername();

        // 1) 이메일 인증 여부 체크
        EmailVerification ev = verifyRepo.findByEmail(username)
                .orElseThrow(() -> new UnauthenticatedEmailException("이메일 인증을 먼저 해주세요."));
        // 코드 일치ㆍ만료 확인 → 통과 못 하면 예외 발생

        if (!ev.isVerified())
            throw new IllegalArgumentException("이메일 인증이 아직 완료되지 않았습니다.");

        // 2) 중복 ID 검사
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateLoginIdException("중복된 아이디로는 가입할 수 없습니다.");
        }

        // 3) 회원 저장
        User user = User.signupBuilder()
                .username(registerUserDto.getUsername())
                .password(bCryptPasswordEncoder.encode(registerUserDto.getPassword()))
                .nickname(registerUserDto.getNickname())
                .name(registerUserDto.getName())
                .role(UserRole.USER)
                .build();

        userRepository.save(user);

        /* 4) 인증 기록 삭제 (한 번 소모) */
        verifyRepo.delete(ev);
    }

    public ResponseUserDto getUsernameAndNickname(String token) {
        String username = jwtUtil.getUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotExistException("존재하지 않는 유저입니다."));

        return ResponseUserDto.entityToDto(user);
    }

    /**
     * 모든 회원을 "경찰"(@skuniv.ac.kr) 과 "일반"으로 분리해서 반환
     */
    public AllUserDto findAllGrouped() {
        List<ResponseUserDto> police = userRepository.findAll().stream()
                .filter(u -> u.getUsername().endsWith("@skuniv.ac.kr"))
                .map(ResponseUserDto::entityToDto)
                .collect(Collectors.toList());

        List<ResponseUserDto> general = userRepository.findAll().stream()
                .filter(u -> !u.getUsername().endsWith("@skuniv.ac.kr"))
                .map(ResponseUserDto::entityToDto)
                .collect(Collectors.toList());

        return new AllUserDto(police, general);
    }

    /**
     * id 로 회원 삭제 (경찰 / 일반 구분 없이)
     */
    public void deleteById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotExistException("존재하지 않는 유저입니다."));
        userRepository.delete(user);
    }
}
