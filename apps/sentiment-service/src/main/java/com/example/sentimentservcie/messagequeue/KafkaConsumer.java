package com.example.sentimentservcie.messagequeue;

import com.example.sentimentservcie.Model.Sentiment;
import com.example.sentimentservcie.Model.SentimentEntity;
import com.example.sentimentservcie.Repository.SentimentRepository;
import com.example.sentimentservcie.Service.OpenAIService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * KafkaConsumer
 * 댓글 이벤트(CREATE/UPDATE/DELETE)를 Kafka로 받아와
 * 감성분석 후 결과를 SentimentEntity(Repository)에 반영하는 서비스.
 *   - Kafka 토픽: my_topic_comments
 *   - ObjectMapper로 payload 파싱, 이벤트타입 분기 처리
 *   - 감성 분석(OpenAI) 후 DB에 insert/update/delete
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final SentimentRepository repository;
    private final OpenAIService openAIService;
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Kafka 리스너 (댓글 토픽)
     * @param raw Kafka에서 수신한 JSON 문자열
     */
    @KafkaListener(topics = "my_topic_comments")
    public void handleCommentEvent(String raw) throws JsonProcessingException {

        Map<String, Object> payload = mapper.readValue(raw, new TypeReference<>() {});
//        Map<String, Object> payload = (Map<String, Object>) outer.get("payload");

        // tombstone(삭제마커), heartbeat 등 방어
//        if (payload == null) {
//            log.debug("skip empty payload");
//            return;
//        }
//        log.info("😖 payload {}", payload);

        // 주요 필드 파싱
        String eventType     = (String) payload.get("event_type");
        log.info("🤑 event_type check {}", eventType);
        Number art      = (Number) payload.get("article_id");   // ⭐ 스네이크 케이스
        Number com      = (Number) payload.get("id");           // comment PK
        String content  = (String) payload.get("content");
        String username = (String) payload.get("username");
        String userRole = (String) payload.get("role");

        // 추가 방어
        if (eventType == null || art == null || com == null || content == null || username == null || userRole == null) {
            log.warn("필수 필드 누락 → skip : {}", payload);
            return;
        }

        /* 감성 분석(UPDATE·CREATE 시 필요) ----------------------------- */
        Sentiment newSentiment = openAIService.classifySentiment(content);

        /* 분기 처리 ---------------------------------------------------- */
        switch (eventType) {
            case "CREATE" -> handleCreate(art, com, username, userRole, newSentiment);
            case "UPDATE" -> handleUpdate(art, com, username, userRole, newSentiment);
            case "DELETE" -> handleDelete(com);
            default       -> log.warn("알 수 없는 eventType : {}", eventType);
        }
    }

    /* ---------- 세부 로직 메서드 ---------- */

    /**
     * CREATE 이벤트 처리
     * - 이미 존재하면 무시, 없으면 새로 저장
     */
    private void handleCreate(Number art, Number com, String username, String userRole, Sentiment sentiment) {

        repository.findByCommentId(com.longValue()).ifPresent(entity -> {
            log.info("이미 존재하는 commentId → 중복 CREATE 무시 : {}", com);
        });

        repository.save(SentimentEntity.builder()
                .articleId(art.longValue())
                .commentId(com.longValue())
                .username(username)
                .userRole(userRole)
                .sentiment(sentiment)
                .build());

        log.info("✅ [CREATE] saved sentiment for comment {}", com);
    }

    /**
     * UPDATE 이벤트 처리
     * - 존재 시 감성만 갱신, 미존재 시 새로 insert
     */
    private void handleUpdate(Number art, Number com, String username, String userRole, Sentiment sentiment) {

        repository.findByCommentId(com.longValue())
                .ifPresentOrElse(entity -> { // 존재 ⇒ 감성/필드 수정
                    entity.setSentiment(sentiment);
                    repository.save(entity);

                    log.info("♻ [UPDATE] updated sentiment for comment {}", com);
                }, () -> {  // 미존재 ⇒ 새로 저장
                    repository.save(SentimentEntity.builder()
                            .articleId(art.longValue())
                            .commentId(com.longValue())
                            .username(username)
                            .userRole(userRole)
                            .sentiment(sentiment)
                            .build());

                    log.info("✅ [UPDATE→CREATE] saved new sentiment for comment {}", com);
                });
    }

    /**
     * DELETE 이벤트 처리
     * - 존재 시 삭제, 없으면 무시
     */
    private void handleDelete(Number com) {
        repository.findByCommentId(com.longValue())
                .ifPresentOrElse(entity -> {
                    repository.delete(entity);
                    log.info("❌ [DELETE] removed sentiment for comment {}", com);
                }, () -> log.info("⚠️ [DELETE] sentiment not found for comment {}", com));
    }
}
