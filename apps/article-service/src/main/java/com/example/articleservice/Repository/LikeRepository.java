package com.example.articleservice.Repository;

import com.example.articleservice.Model.Article;
import com.example.articleservice.Model.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    // 특정 유저가 해당 기사에 좋아요 눌렀는지 여부
    boolean existsByArticleAndUsername(Article article, String username);

    // 특정 유저가 해당 기사에 누른 좋아요 엔티티 조회
    Optional<Like> findByArticleAndUsername(Article article, String username);

    // 해당 기사에 달린 좋아요 수
    Long countByArticle(Article article);
}