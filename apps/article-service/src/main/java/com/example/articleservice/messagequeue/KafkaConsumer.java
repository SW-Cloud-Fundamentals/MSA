package com.example.articleservice.messagequeue;

import com.example.articleservice.Dto.Comment.CommentMessage;
import com.example.articleservice.Model.Article;
import com.example.articleservice.Model.Comment;
import com.example.articleservice.Repository.ArticleRepository;
import com.example.articleservice.Repository.CommentRepository;
import com.example.articleservice.Service.RankService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {

    private final CommentRepository commentRepo;
    private final ArticleRepository articleRepo;
    private final RankService rankService;
    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = "my_topic_comments")       // 필요 시 스레드 확장
    public void handleCommentEvent(String raw) throws JsonProcessingException {

        Map<String,Object> outer = mapper.readValue(raw, new TypeReference<>() {});
        if (outer.containsKey("schema")) {               // Debezium/JDBC Source 형식
            log.debug("skip CDC message");
            return;
        }

        Map<String,Object> payload = outer;

        // 주요 필드 파싱
        String eventType = (String) payload.getOrDefault("eventType", payload.get("event_type"));
        log.info("🤑 event_type check {}", eventType);
        Number art       = (Number) payload.getOrDefault("articleId", payload.get("article_id"));
        Number com        = (Number) payload.get("id");
        String content    = (String) payload.get("content");
        String username   = (String) payload.get("username");
        String userRole  = (String) payload.getOrDefault("userRole", payload.get("role"));

        // 추가 방어
        if (eventType == null || art == null || com == null || content == null || username == null || userRole == null) {
            log.warn("필수 필드 누락 → skip : {}", payload);
            return;
        }

        CommentMessage e = mapper.convertValue(payload, CommentMessage.class);

        switch (e.getEventType()) {
            case "CREATE" -> handleCreate(e);
            case "UPDATE" -> handleUpdate(e);
            case "DELETE" -> handleDelete(e);
            default -> log.warn("❓ unknown eventType {}", e.getEventType());
        }
    }

    /* ---------- 세부 로직 ---------- */

    private void handleCreate(CommentMessage e) {
        if (commentRepo.existsById(e.getId())) return; // idempotent

        Article article = articleRepo.findById(e.getArticleId())
                .orElseThrow(() -> new IllegalStateException("article not found"));

        Comment entity = Comment.builder()
                .id(e.getId())                           // ❶ 컨트롤러가 준 PK
                .eventType(e.getEventType())
                .username(e.getUsername())
                .nickname(e.getNickname())
                .role(e.getUserRole())
                .content(e.getContent())
                .articleId(article)
                .build();

        commentRepo.save(entity);
        article.setCommentCount(article.getCommentCount() + 1);
        rankService.updateScore(article.getId(), 0, +1);
        log.info("✅ CREATE persisted {}", e.getId());
    }

    private void handleUpdate(CommentMessage e) {
        commentRepo.findById(e.getId())
                .ifPresentOrElse(c -> {
                    c.setContent(e.getContent());
                    commentRepo.save(c);
                    log.info("♻ UPDATE {}", e.getId());
                }, () -> handleCreate(e)); // 없으면 업서트
    }

    private void handleDelete(CommentMessage e) {
        commentRepo.findById(e.getId()).ifPresent(c -> {
            c.setDeleted(true);             // soft-delete
            rankService.updateScore(c.getArticleId().getId(), 0, -1);
            log.info("❌ DELETE {}", e.getId());
        });
    }
}

