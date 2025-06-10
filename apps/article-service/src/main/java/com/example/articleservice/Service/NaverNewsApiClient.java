package com.example.articleservice.Service;

import com.example.articleservice.Dto.News.NaverNewsResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class NaverNewsApiClient {

    @Value("${naver.client-id}")
    private String clientId;
    @Value("${naver.client-secret}")
    private String clientSecret;

    private WebClient webClient;

    private int display = 20;
    private String sort = "date"; // date = 날짜순, sim = 정확도순

    @PostConstruct  // 빈이 생성되고 의존성 주입이 끝난 뒤에 자동으로 실행
    private void init() {
        this.webClient = WebClient.builder()
                .baseUrl("https://openapi.naver.com")
                .defaultHeader("X-Naver-Client-Id", clientId)
                .defaultHeader("X-Naver-Client-Secret", clientSecret)
                .build();
    }

    public NaverNewsResponse searchNews(String keyword) {
        String json = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/search/news.json")
                        .queryParam("query", keyword)
                        .queryParam("display", display)
                        .queryParam("sort", sort)
                        .build())
                .retrieve()
                .onStatus(status -> status.isError(),
                        res -> res.bodyToMono(String.class)
                                .map(msg -> new RuntimeException("Naver API error: " + msg)))
                .bodyToMono(String.class)
                .block();

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, NaverNewsResponse.class);
        } catch (JsonProcessingException e) {
            // 필요하면 로깅 추가
            throw new RuntimeException("네이버 뉴스 응답 파싱 실패", e);
        }
    }
}
