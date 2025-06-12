package com.example.sentimentservcie.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OpenAIConfig {
    @Value("${openai.api-key}")
    private String openAiKey;

    /**
     * OpenAI HTTP 통신용 WebClient 빈 등록
     * @return WebClient 인스턴스 (Authorization 헤더 기본 설정)
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://api.openai.com")
                .defaultHeader("Authorization", "Bearer " + openAiKey)
                .build();
    }
}