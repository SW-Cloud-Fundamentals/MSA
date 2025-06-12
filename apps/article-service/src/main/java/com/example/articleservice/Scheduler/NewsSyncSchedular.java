package com.example.articleservice.Scheduler;

import com.example.articleservice.Service.NewsSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsSyncSchedular {
    private final NewsSearchService newsSearchService;

    /** 매 30분마다 실행 (cron: 초 분 시 일 월 요일) */
    @Scheduled(cron = "0 */30 * * * *")
    public void everyFiveMinutes() {
        LocalDateTime now = LocalDateTime.now();
        newsSearchService.syncFromNaver();
        log.debug("💨 Article Update at {}", now);
    }
}
