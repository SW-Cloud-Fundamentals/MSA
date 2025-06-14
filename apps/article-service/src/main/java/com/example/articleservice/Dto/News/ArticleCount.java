package com.example.articleservice.Dto.News;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ArticleCount {
    private String keyword;
    private long count;
}
