package com.example.userservice.service;

import com.example.userservice.exception.CodeExpiredException;
import com.example.userservice.exception.InvalidEmailDomainException;
import com.example.userservice.exception.UnverifiedCodeException;
import com.example.userservice.model.EmailVerification;
import com.example.userservice.repository.EmailVerificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailVerificationRepository verifyRepo;

    private static final Duration TTL = Duration.ofMinutes(3);

    /**
     * 인증번호 전송
     */
    public void sendPoliceCode(String email) {
        // 혹시 몰라 이중 방어 한 줄
        if (!email.endsWith("@skuniv.ac.kr")) {
            throw new InvalidEmailDomainException("@skuniv.ac.kr 주소만 사용할 수 있습니다.");
        }

        String code = String.format("%06d",
                ThreadLocalRandom.current().nextInt(0, 1_000_000));

        // DB에 upsert
        verifyRepo.findByEmail(email).ifPresentOrElse(
                ev -> {   // 있으면 갱신
                    ev.setCode(code);
                    ev.setExpiry(LocalDateTime.now().plus(TTL));
                    ev.setVerified(false);
                },
                () -> verifyRepo.save(EmailVerification.builder()
                        .email(email)
                        .code(code)
                        .expiry(LocalDateTime.now().plus(TTL))
                        .verified(false)
                        .build())
        );

        // 메일 발송
        try {
            MimeMessage message = mailSender.createMimeMessage();

            // true → multipart/alternative 로 만들 수 있음
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "utf-8");

            /* 1) From: Gmail 계정과 동일 + 서비스명 라벨 */
            helper.setFrom("skusofting1@gmail.com", "오늘의 현장");   // ★ 추가

            /* 2) To */
            helper.setTo(email);

            /* 3) Subject */
            helper.setSubject("[오늘의 현장] 이메일 인증번호입니다");

            /* 4) 본문 – HTML & Plain 동시 전송 */
            String plain = """
                    안녕하세요, 오늘의 현장 서비스입니다.
                    
                    아래 인증번호를 3분 이내에 입력해 주세요.
                    
                    인증번호: %s
                    """.formatted(code);

            String html = """
                    <p>안녕하세요, <strong>오늘의 현장</strong> 서비스입니다.</p>
                    <p>경찰 전용 인증번호 입니다.</p>
                    <p>아래 <b>인증번호</b>를 <span style="color:#0066ff;">3분 이내</span>에 입력해 주세요.</p>
                    <h2 style="letter-spacing:4px;">%s</h2>
                    <p style="font-size:12px;color:#888;">본 메일은 발신 전용입니다.</p>
                    """.formatted(code);

            helper.setText(plain, html);   // (plain, html) 순서

            /* 5) 전송 */
            mailSender.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new IllegalStateException("메일 전송 실패", e);
        }
    }

    public void sendGeneralCode(String email) {
        String code = String.format("%06d",
                ThreadLocalRandom.current().nextInt(0, 1_000_000));

        // DB에 upsert
        verifyRepo.findByEmail(email).ifPresentOrElse(
                ev -> {   // 있으면 갱신
                    ev.setCode(code);
                    ev.setExpiry(LocalDateTime.now().plus(TTL));
                    ev.setVerified(false);
                },
                () -> verifyRepo.save(EmailVerification.builder()
                        .email(email)
                        .code(code)
                        .expiry(LocalDateTime.now().plus(TTL))
                        .verified(false)
                        .build())
        );

        // 메일 발송
        try {
            MimeMessage message = mailSender.createMimeMessage();

            // true → multipart/alternative 로 만들 수 있음
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "utf-8");

            /* 1) From: Gmail 계정과 동일 + 서비스명 라벨 */
            helper.setFrom("skusofting1@gmail.com", "오늘의 현장");   // ★ 추가

            /* 2) To */
            helper.setTo(email);

            /* 3) Subject */
            helper.setSubject("[오늘의 현장] 이메일 인증번호입니다");

            /* 4) 본문 – HTML & Plain 동시 전송 */
            String plain = """
                    안녕하세요, 오늘의 현장 서비스입니다.
                    
                    아래 인증번호를 3분 이내에 입력해 주세요.
                    
                    인증번호: %s
                    """.formatted(code);

            String html = """
                    <p>안녕하세요, <strong>오늘의 현장</strong> 서비스입니다.</p>
                    <p>아래 <b>인증번호</b>를 <span style="color:#0066ff;">3분 이내</span>에 입력해 주세요.</p>
                    <h2 style="letter-spacing:4px;">%s</h2>
                    <p style="font-size:12px;color:#888;">본 메일은 발신 전용입니다.</p>
                    """.formatted(code);

            helper.setText(plain, html);   // (plain, html) 순서

            /* 5) 전송 */
            mailSender.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new IllegalStateException("메일 전송 실패", e);
        }
    }

    /**
     * 인증번호 확인
     */
    public void verifyCode(String email, String inputCode) {
        EmailVerification ev = verifyRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("코드 발급 기록이 없습니다."));

        if (ev.getExpiry().isBefore(LocalDateTime.now()))
            throw new CodeExpiredException("인증번호가 만료되었습니다.");

        if (!ev.getCode().equals(inputCode))
            throw new UnverifiedCodeException("인증번호가 일치하지 않습니다.");

//        verifyRepo.delete(ev); // 단발성 사용 후 삭제
        ev.setVerified(true);
    }
}
