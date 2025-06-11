package com.example.articleservice.Repository;

import com.example.articleservice.Model.Article;
import com.example.articleservice.Model.Comment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByUsername(String username);

    List<Comment> findByArticleId(Article article);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.articleId.id = :articleId")
    Long countByArticleId(@Param("articleId") Long articleId);

    List<Comment> findByArticleIdAndDeletedFalse(Article article);

    /**
     * deleted = true 인 댓글을 모두 물리적으로 삭제하고,
     * 삭제된 행 수를 반환합니다.
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.deleted = true")
    int purgeSoftDeleted();
}