package com.example.userservice.Scheduler;

import com.example.userservice.repository.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailVerificationScheduler {

    private final EmailVerificationRepository verifyRepo;

    /** 매 30분마다 만료된 인증 코드 삭제 */
    @Scheduled(cron = "0 */30 * * * *")   // “초 분 시 일 월 요일”
    public void clearExpiredCodes() {
        LocalDateTime now = LocalDateTime.now();
        verifyRepo.deleteAllByExpiryBefore(now);
        log.debug("Expired email auth codes cleaned at {}", now);
    }
}