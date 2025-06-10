package com.example.sentimentservcie.Service;

import com.example.sentimentservcie.Model.Sentiment;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenAIService {

    private final WebClient webClient;

    @Value("${openai.model}")
    private String model;

    /**
     * 주어진 문장을 POSITIVE / NEGATIVE / NEUTRAL 중 하나로 분류한다.
     */
    public Sentiment classifySentiment(String text) {

        /* 1) 채팅 요청 JSON ― max_tokens 는 3 정도면 충분 */
        String body = """
            {
              "model": "%s",
              "temperature": 0,
              "max_tokens": 3,
              "stop": ["\\n"],
              "messages": [
                {
                  "role": "system",
                  "content": "Reply with exactly ONE of these UPPER-CASE words: POSITIVE, NEGATIVE, or NEUTRAL."
                },
                {
                  "role": "user",
                  "content": "%s"
                }
              ]
            }
            """.formatted(model, text.replace("\"", "\\\""));

        try {
            /* 2) OpenAI 요청 */
            String label = webClient.post()
                    .uri("/v1/chat/completions")
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .map(j -> j.at("/choices/0/message/content").asText()) // 원본 그대로
                    .block(Duration.ofSeconds(10));

            /* 3) 후처리 ― 대문자 · 공백 · 특수문자 제거 */
            label = label.toUpperCase(Locale.ROOT).replaceAll("[^A-Z]", "");

            /* 4) 잘린 ‘NEUT…’ 패턴 보정 */
            if (label.startsWith("NEUT")) label = "NEUTRAL";

            /* 5) enum 매핑. 알 수 없는 값은 NEUTRAL 로 폴백 */
            return switch (label) {
                case "POSITIVE" -> Sentiment.POSITIVE;
                case "NEGATIVE" -> Sentiment.NEGATIVE;
                default         -> Sentiment.NEUTRAL;
            };

        } catch (WebClientResponseException e) {
            log.error("OpenAI {} : {}", e.getStatusCode(), e.getResponseBodyAsString());
            return Sentiment.NEUTRAL;              // 네트워크·API 오류 → 중립
        } catch (Exception e) {
            log.error("OpenAI 호출 실패", e);        // 예기치 못한 예외 → 중립
            return Sentiment.NEUTRAL;
        }
    }
}
