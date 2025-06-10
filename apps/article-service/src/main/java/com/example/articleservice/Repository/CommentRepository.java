package com.example.articleservice.Repository;

import com.example.articleservice.Model.Article;
import com.example.articleservice.Model.Comment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByUsername(String username);

    List<Comment> findByArticleId(Article article);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.articleId.id = :articleId")
    Long countByArticleId(@Param("articleId") Long articleId);
}