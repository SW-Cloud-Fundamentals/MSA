package com.example.articleservice.Controller;

import com.example.articleservice.Service.RankService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rank")
public class RankController {

    private final RankService rankService;

    /** 1️⃣ 내림차순 상위 10개의 articleId 값들 조회 */
    @GetMapping
    public List<Long> top(@RequestParam(defaultValue = "10") int size) {
        return rankService.topN(size);
    }
}
