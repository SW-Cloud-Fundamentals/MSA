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

    @GetMapping("/{articleId}")
    public ResponseEntity<ResponseDTO<List<ArticleSentimentSummary>>> getSentimentSummary(@PathVariable Long articleId) {

        List<ArticleSentimentSummary> summaries = sentimentService.getSentimentSummary(articleId);

        // 📣 수정 필요
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_GET_COMMENT, summaries));
    }
}
