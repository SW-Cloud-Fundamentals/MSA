package com.example.sentimentservcie.Service;

import com.example.sentimentservcie.Code.ErrorCode;
import com.example.sentimentservcie.Dto.ArticleSentimentSummary;
import com.example.sentimentservcie.Exception.CustomException;
import com.example.sentimentservcie.Model.Sentiment;
import com.example.sentimentservcie.Model.SentimentEntity;
import com.example.sentimentservcie.Repository.SentimentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SentimentService {

    private final SentimentRepository sentimentRepository;

    /** 특정 기사에 대한 댓글 감정 통계 집계 */
    public List<ArticleSentimentSummary> getSentimentSummary(Long articleId) {
        List<SentimentEntity> entities = sentimentRepository.findByArticleId(articleId);

        Map<String, Map<Sentiment, Long>> grouped =
                entities.stream()
                        .collect(Collectors.groupingBy(
                                SentimentEntity::getUserRole,          // POLICE / USER
                                Collectors.groupingBy(
                                        SentimentEntity::getSentiment, // POSITIVE / NEGATIVE / NEUTRAL
                                        Collectors.counting()
                                )
                        ));

        return grouped.entrySet().stream()
                .map(e -> toSummary(e.getKey(), e.getValue()))
                .toList();
    }

    /** ArticleSentimentSummary DTO 변환 헬퍼 */
    private ArticleSentimentSummary toSummary(String userRole,
                                              Map<Sentiment, Long> m) {
        return new ArticleSentimentSummary(
                userRole,                                    // :contentReference[oaicite:1]{index=1}
                m.getOrDefault(Sentiment.POSITIVE, 0L).intValue(),
                m.getOrDefault(Sentiment.NEGATIVE, 0L).intValue(),
                m.getOrDefault(Sentiment.NEUTRAL,  0L).intValue()
        );                                              // DTO 정의 :contentReference[oaicite:2]{index=2}
    }
}
