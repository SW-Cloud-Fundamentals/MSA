package com.example.articleservice.Controller;

import com.example.articleservice.Code.ResponseCode;
import com.example.articleservice.Dto.News.ArticleDetail;
import com.example.articleservice.Dto.News.ArticleListResponse;
import com.example.articleservice.Dto.Response.ResponseDTO;
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

    /** 1️⃣ 사용자가 입력한 키워드를 지정한 기사들 조회 */
    @GetMapping
    public ResponseEntity<ResponseDTO<List<ArticleListResponse>>> searchList(
            @RequestParam(required = false, defaultValue = "마약") String keyword
    ) {
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_GET_ARTICLE, newsSearchService.searchList(keyword)));
    }

    /** 2️⃣ articleId 값으로 해당 기사의 상세정보 조회 */
    @GetMapping("/{articleId}")
    public ResponseEntity<ResponseDTO<ArticleDetail>> searchDetail(@PathVariable("articleId") Long articleId) {
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_GET_ARTICLE, newsSearchService.searchDetail(articleId)));
    }

    /** 3️⃣ 사용자가 입력한 키워드와 날짜에 맞는 기사들 조회 */
    @GetMapping("/search")
    public ResponseEntity<ResponseDTO<List<ArticleListResponse>>> searchByKeywordAndDate(
            @RequestParam String keyword,
            @RequestParam(required = false) String date
    ) {
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_GET_ARTICLE, newsSearchService.searchByKeywordAndDate(keyword, date)));
    }
}
