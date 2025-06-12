package com.example.articleservice.Model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "articles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String keyword;

    private String thumbTitle;

    @Column(unique = true)
    private String link;

    private String description;

    private String pubDate;

    private String imageUrl;

    private LocalDateTime createdAt;

    private String title;

    @Lob
    private String content;

    // 공감 수 (기본값 0)
    @Column(nullable = false)
    private Long likes = 0L;

    // 댓글 수 (기본값 0)
    @Column(nullable = false)
    private Long commentCount = 0L;

    /** INSERT 직전 → createdAt 을 now() */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public static Article fromRequestDto(String thumbTitle, String link, String description, String pubDate, String keyword) {
        return Article.builder()
                .thumbTitle(thumbTitle)
                .link(link)
                .description(description)
                .pubDate(pubDate)
                .keyword(keyword)
                .likes(0L)
                .commentCount(0L)
                .build();
    }
}
