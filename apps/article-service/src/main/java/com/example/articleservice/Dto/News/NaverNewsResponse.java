package com.example.articleservice.Dto.News;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/** 실제 네이버 뉴스 api로 제공받는 값들입니다.
 * 실제 기사 URL과 OriginalLink를 제공하기 때문에
 * 웹 크롤링 할 때 필요합니다.
 * */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverNewsResponse {
    private List<Item> items;

    @Getter @Setter
    public static class Item {
        private String title;
        private String link;        // 실제 기사 URL
        private String originallink;
        private String description;
        private String pubDate;
    }
}
