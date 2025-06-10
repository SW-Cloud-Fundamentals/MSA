package com.example.articleservice.Controller;

import com.example.articleservice.Dto.News.ArticleDetail;
import com.example.articleservice.Dto.News.ArticleListResponse;
import com.example.articleservice.Service.NewsSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {
    private final NewsSearchService newsSearchService;

    @GetMapping
    public ResponseEntity<List<ArticleListResponse>> searchList(
            @RequestParam(required = false, defaultValue = "마약") String keyword
    ) {
        return ResponseEntity.ok(newsSearchService.searchList(keyword));
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleDetail> searchDetail(@PathVariable("articleId") Long articleId) {
        return ResponseEntity.ok(newsSearchService.searchDetail(articleId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ArticleListResponse>> searchByKeywordAndDate(
            @RequestParam String keyword,
            @RequestParam(required = false) String date
    ) {
        return ResponseEntity.ok(newsSearchService.searchByKeywordAndDate(keyword, date));
    }
}
