package com.example.articleservice.Dto.News;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ArticleDetail {
    private Long id;
    private String title;
    private String pubDate;
    private String Content;              // 본문 전체 텍스트
    private String imageUrl;
    private Long likes;
    private Long commentCount;
}
