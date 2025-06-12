package com.example.articleservice.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * 기사 인기 점수를 Redis Sorted Set(ZSet)으로 관리.
 * • Score = 좋아요(like) + 댓글(comment) * 0.3
 * • ZSET 키: article:rank
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RankService {

    private final StringRedisTemplate redis;
    private static final String RANK_KEY = "article:rank";

    /**
     * 점수 업데이트
     * @param articleId    대상 기사 ID
     * @param likeDelta    좋아요 증감(예: +1, -1)
     * @param commentDelta 댓글 증감(예: +1, -1)
     */
    public void updateScore(Long articleId, long likeDelta, long commentDelta) {
        long likes = likeDelta;
        long comments = commentDelta;

        double score = likes + comments * 0.3;
        redis.opsForZSet().incrementScore(RANK_KEY, articleId.toString(), score);
    }

    /** 점수 내림차순 상위 N개 articleId 목록 반환 */
    public List<Long> topN(int n) {
        Set<String> raw =
                redis.opsForZSet().reverseRange(RANK_KEY, 0, n - 1);
        return raw.stream().map(Long::valueOf).toList();
    }

    /** 특정 기사 순위 조회(0부터 시작). 데이터 없으면 -1 반환 */
    public long rankOf(Long articleId) {
        Long rank = redis.opsForZSet().reverseRank(RANK_KEY, articleId.toString());
        return rank == null ? -1 : rank;
    }
}
