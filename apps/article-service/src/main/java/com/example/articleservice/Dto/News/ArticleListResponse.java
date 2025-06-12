package com.example.articleservice.Dto.News;

import com.example.articleservice.Model.Article;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** 네이버 뉴스 api로 가져온 기사 정보들을 담아 반환할 dto 입니다.
 * List로 된 기사들을 조회할 때 사용됩니다.
 * */
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
