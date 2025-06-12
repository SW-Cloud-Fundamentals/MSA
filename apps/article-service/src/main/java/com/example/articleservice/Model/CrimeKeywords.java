package com.example.articleservice.Model;

import java.util.List;

/** 클라이언트가 요청할 수 있는 뉴스 키워드 목록 */
public final class CrimeKeywords {
    public static final List<String> LIST = List.of(
            "마약", "성폭행", "사기", "살인", "방화", "폭행"
    );
    private CrimeKeywords() {}
}