package com.example.articleservice.Dto.Like;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 공감과 관련된 모든 api 호출 시에 반환 될 json dto */
@Getter
@AllArgsConstructor
public class ResponseLike {
    private boolean liked;
    private long likes;
}

