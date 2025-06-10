package com.example.articleservice.Dto.News;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
