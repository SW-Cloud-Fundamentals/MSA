package com.example.articleservice.Controller;

import com.example.articleservice.Code.ErrorCode;
import com.example.articleservice.Code.ResponseCode;
import com.example.articleservice.Dto.Like.ResponseLike;
import com.example.articleservice.Dto.Response.ResponseDTO;
import com.example.articleservice.Exception.CustomException;
import com.example.articleservice.Model.Article;
import com.example.articleservice.Repository.ArticleRepository;
import com.example.articleservice.Service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/articles/like")
public class  LikeController {

    private final LikeService likeService;
    private final ArticleRepository articleRepository;

    /** 1️⃣ 공감 토글 엔드포인트 */
    @PostMapping("/{articleId}")
    public ResponseEntity<ResponseDTO<ResponseLike>> like(@PathVariable Long articleId,
                                                         @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring("Bearer ".length());
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));

        boolean alreadyLiked = likeService.hasUserLikedArticle(article, token);

        if (alreadyLiked) {
            likeService.cancelLike(articleId, token);
        } else {
            likeService.likeArticle(articleId, token);
        }

        boolean liked = !alreadyLiked;
        long likeCount = likeService.countLikes(articleId);

        ResponseLike responseLike = new ResponseLike(liked, likeCount);

        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_LIKE, responseLike));
    }

    /** 2️⃣ 공감 상태 + 수 조회 */
    @GetMapping("/{articleId}")
    public ResponseEntity<ResponseDTO<ResponseLike>> getLike(@PathVariable Long articleId,
                                                @RequestHeader("Authorization") String authHeader) {
        boolean liked = false;

        String token = authHeader.substring("Bearer ".length());

        try {
            Article article = articleRepository.findById(articleId)
                    .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));
            liked = likeService.hasUserLikedArticle(article, token);
        } catch (Exception ignored) {
            // 로그인 안 했으면 liked = false
        }

        long likeCount = likeService.countLikes(articleId);

        ResponseLike responseLike = new ResponseLike(liked, likeCount);

        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_LIKE_COUNT, responseLike));
    }
}
