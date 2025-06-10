package com.example.sentimentservcie.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sentiments")
public class SentimentEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long articleId;

    @Column
    private String username;

    @Column
    private String userRole;

    @Column
    private Long commentId;

    @Column
    private Sentiment sentiment;
}
