package com.example.articleservice.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "article_like",
        uniqueConstraints = @UniqueConstraint(columnNames = {"article_id", "user_id"})
)
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🟡 Article에 대한 좋아요
    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    // 🟡 좋아요 누른 사용자
    @Column(nullable = false)
    private String username;
}
