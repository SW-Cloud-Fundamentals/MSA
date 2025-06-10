package com.example.sentimentservcie.Repository;

import com.example.sentimentservcie.Model.SentimentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SentimentRepository extends JpaRepository<SentimentEntity, Long> {
    List<SentimentEntity> findByArticleId(Long articleId);
}
