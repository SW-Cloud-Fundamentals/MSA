package com.example.articleservice.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class RankService {

    private final StringRedisTemplate redis;
    private static final String RANK_KEY = "article-test:rank";

    public void updateScore(Long postId, long likeDelta, long commentDelta) {
        long likes = likeDelta;
        long comments = commentDelta;

        double score = likes + comments * 0.3;
        redis.opsForZSet().incrementScore(RANK_KEY, postId.toString(), score);
    }

    // 점수 내림차순 상위 N개 postId 목록 반환
    public List<Long> topN(int n) {
        Set<String> raw =
                redis.opsForZSet().reverseRange(RANK_KEY, 0, n - 1);
        return raw.stream().map(Long::valueOf).toList();
    }

    // 해당 postId가 몇 위인지 반환
    public long rankOf(Long postId) {
        Long rank = redis.opsForZSet().reverseRank(RANK_KEY, postId.toString());
        return rank == null ? -1 : rank;
    }
}
