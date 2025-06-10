package com.example.articleservice.Dto.Like;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseLike {
    private boolean liked;
    private long likes;
}

