package com.example.articleservice.Service;

import com.example.articleservice.Code.ErrorCode;
import com.example.articleservice.Exception.CustomException;
import com.example.articleservice.Model.Article;
import com.example.articleservice.Model.Like;
import com.example.articleservice.Repository.ArticleRepository;
import com.example.articleservice.Repository.LikeRepository;
import com.example.jwt_util.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final ArticleRepository articleRepository;
    private final JWTUtil jwtUtil;
    private final RankService rankService;

    /** 1️⃣ 공감 생성 */
    @Transactional
    public void likeArticle(Long articleId, String token) {
        String username = jwtUtil.getUsername(token);

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));

        if (likeRepository.existsByArticleAndUsername(article, username)) {
            throw new CustomException(ErrorCode.ALREADY_LIKED);
        }

        Like like = Like.builder()
                .article(article)
                .username(username)
                .build();

        likeRepository.save(like);

        // 공감 수 증가
        article.setLikes(article.getLikes() + 1);

        rankService.updateScore(articleId, +1, 0);
    }

    /** 2️⃣ 공감 취소 */
    @Transactional
    public void cancelLike(Long articleId, String token) {
        String username = jwtUtil.getUsername(token);

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));

        Like like = likeRepository.findByArticleAndUsername(article, username)
                .orElseThrow(() -> new CustomException(ErrorCode.LIKE_NOT_FOUND));

        likeRepository.delete(like);

        // 공감 수 감소
        article.setLikes(Math.max(0, article.getLikes() - 1));

        rankService.updateScore(articleId, -1, 0);
    }

    /** 3️⃣ 특정 기사의 공감 수 조회 */
    public Long countLikes(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));

        return likeRepository.countByArticle(article);
    }

    /** 4️⃣ 특정 유저의 공감 || 비공감 상태 조회 */
    public boolean hasUserLikedArticle(Article article, String token) {
        String username = jwtUtil.getUsername(token);

        return likeRepository.existsByArticleAndUsername(article, username);
    }
}
