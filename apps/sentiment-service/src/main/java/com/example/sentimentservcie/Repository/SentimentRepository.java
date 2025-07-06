package com.example.sentimentservcie.Repository;

import com.example.sentimentservcie.Model.SentimentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SentimentRepository extends JpaRepository<SentimentEntity, Long> {
    List<SentimentEntity> findByArticleId(Long articleId);

    Optional<SentimentEntity> findByCommentId(Long commentId);

    boolean existsByCommentId(long l);
}
