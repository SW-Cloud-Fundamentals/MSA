package com.example.sentimentservcie.Controller;

import com.example.sentimentservcie.Code.ResponseCode;
import com.example.sentimentservcie.Dto.ArticleSentimentSummary;
import com.example.sentimentservcie.Dto.ResponseDTO;
import com.example.sentimentservcie.Service.SentimentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sentiments")
@RequiredArgsConstructor
public class SentimentController {

    private final SentimentService sentimentService;

    /**
     * 기사 ID로 감성 통계 조회
     * @param articleId 대상 기사 ID
     * @return 감성 통계 리스트(작성자 role별 등 분리 가능)
     */
    @GetMapping("/{articleId}")
    public ResponseEntity<ResponseDTO<List<ArticleSentimentSummary>>> getSentimentSummary(@PathVariable Long articleId) {

        List<ArticleSentimentSummary> summaries = sentimentService.getSentimentSummary(articleId);

        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_GET_SENTIMENT, summaries));
    }
}
