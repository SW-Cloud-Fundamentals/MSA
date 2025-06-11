package com.example.articleservice.Scheduler;

import com.example.articleservice.Repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentCleanupScheduler {
    private final CommentRepository commentRepository;

    /** 매 분 0초마다 실행 (cron: 초 분 시 일 월 요일) */
    @Scheduled(cron = "0 */1 * * * *")
    @Transactional
    public void clearSoftDeletedComments() {
        int removed = commentRepository.purgeSoftDeleted();
        if (removed > 0) {
            log.info("🧹 Purged {} soft-deleted comments", removed);
        }
    }
}
