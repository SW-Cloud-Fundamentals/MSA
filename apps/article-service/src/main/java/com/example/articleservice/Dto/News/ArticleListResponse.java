package com.example.articleservice.Dto.News;

import com.example.articleservice.Model.Article;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ArticleListResponse {
    private Long id;
    private String title;
    private String link;
    private String description;
    private String pubDate;
    private String imageUrl;
    private Long likes;
    private Long commentCount;

    public static ArticleListResponse entityToDto(Article article, Long commentCount) {
        return ArticleListResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .link(article.getLink())
                .description(article.getDescription())
                .pubDate(article.getPubDate())
                .imageUrl(article.getImageUrl())
                .likes(article.getLikes())
                .commentCount(commentCount)
                .build();
    }
}
