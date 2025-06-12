package com.example.articleservice.Dto.News;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 네이버 뉴스 api를 통해 뉴스 기사들을 가져오면
 * 제목과 본문이 일정 텍스트 이상부터는 짤리고,
 * image는 제공하지 않습니다.
 * 기사 link는 제공하기 때문에 해당 link로 웹 크롤링을 실시하고나서
 * 크롤링 값들을 저장, 반환할 때 필요한 클래스입니다.
 * */
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
