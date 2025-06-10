package com.example.sentimentservcie.messagequeue;

import com.example.sentimentservcie.Dto.SentimentDto;
import com.example.sentimentservcie.Model.Sentiment;
import com.example.sentimentservcie.Model.SentimentEntity;
import com.example.sentimentservcie.Repository.SentimentRepository;
import com.example.sentimentservcie.Service.OpenAIService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.example.sentimentservcie.Model.Sentiment.POSITIVE;

@Service
@Slf4j
public class KafkaConsumer {
    SentimentRepository repository;
    OpenAIService openAIService;

    @Autowired
    public KafkaConsumer(SentimentRepository repository, OpenAIService openAIService) {
        this.repository = repository;
        this.openAIService = openAIService;
    }

    @KafkaListener(topics = "my_topic_comments")
    public void createSentiment(String raw) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> outer = mapper.readValue(raw, new TypeReference<>() {});
        Map<String, Object> payload = (Map<String, Object>) outer.get("payload");

        if (payload == null) {      // tombstone/heartbeat 방어
            log.debug("skip empty payload");
            return;
        }

        Number art = (Number) payload.get("article_id");   // ⭐ 스네이크 케이스
        Number com = (Number) payload.get("id");           // comment PK
        String content = (String) payload.get("content");
        String username = (String) payload.get("username");
        String userRole = (String) payload.get("role");

        if (art == null || com == null || content == null || username == null || userRole == null) {                  // 추가 방어
            log.warn("필수 필드 누락 → skip : {}", payload);
            return;
        }

        Sentiment s = openAIService.classifySentiment(content);

        SentimentEntity entity = SentimentEntity.builder()
                .articleId(art.longValue())
                .username(username)
                .userRole(userRole)
                .commentId(com.longValue())
                .sentiment(s)
                .build();

        repository.save(entity);                           // 정상 처리 → 오프셋 커밋
        log.info("✅ saved sentiment {}", entity);
    }
}
