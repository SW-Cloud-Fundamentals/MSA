package com.example.articleservice.Repository;

import com.example.articleservice.Model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    boolean existsByLink(String link);

    Optional<Article> findByLink(String link);

    List<Article> findByKeywordOrderByPubDate(String keyword);
}
