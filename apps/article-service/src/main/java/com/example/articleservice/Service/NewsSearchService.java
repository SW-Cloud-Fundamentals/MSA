package com.example.articleservice.Service;

import com.example.articleservice.Dto.News.ArticleDetail;
import com.example.articleservice.Dto.News.ArticleListResponse;
import com.example.articleservice.Dto.News.NaverNewsResponse;
import com.example.articleservice.Model.Article;
import com.example.articleservice.Model.CrimeKeywords;
import com.example.articleservice.Repository.ArticleRepository;
import com.example.articleservice.Repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsSearchService {

    private final CommentRepository commentRepository;
    private final NaverNewsApiClient naverClient;
    private final ArticleRepository articleRepository;
    private static final Pattern NAVER_ARTICLE =
            Pattern.compile("^https?://n\\.news\\.naver\\.com/.*");

    @Transactional
    public void syncFromNaver() {
        CrimeKeywords.LIST.forEach(keyword -> {
            List<NaverNewsResponse.Item> items = naverClient.searchNews(keyword).getItems();

            for (var item : items) {
                // 0) n.news.naver.com 패턴이 아니면 처리하지 않고 건너뛰기
                if (!NAVER_ARTICLE.matcher(item.getLink()).find()) {
                    continue;
                }

                // 1) 먼저 변수 선언
                Article article;

                // 2) 링크 중복 체크 — 있으면 기존 조회, 없으면 새 객체 생성
                if (articleRepository.existsByLink(item.getLink())) {
                    article = articleRepository.findByLink(item.getLink())
                            .orElseThrow(() -> new EntityNotFoundException("링크로 조회 실패: " + item.getLink()));
                } else {
                    article = Article.fromRequestDto(
                            item.getTitle(),
                            item.getLink(),
                            item.getDescription(),
                            item.getPubDate(),
                            keyword
                    );
                }

                // 3) 본문·이미지 크롤링 (이제 무조건 NAVER_ARTICLE 패턴이므로 바로 실행)
                ArticleDetail detail = crawlNaverArticle(item);
                article.setTitle(detail.getTitle());
                article.setContent(detail.getContent());
                article.setImageUrl(detail.getImageUrl());
                article.setKeyword(keyword);

                // 4) DB에 저장
                articleRepository.save(article);
            }
        });
    }

    public List<ArticleListResponse> searchList(String keyword) {
        return articleRepository.findByKeywordOrderByPubDate(keyword).stream()
                .map(article -> {
                    Long commentCount = commentRepository.countByArticleId(article.getId());
                    return ArticleListResponse.builder()
                            .id(article.getId())
                            .title(article.getTitle())
                            .link(article.getLink())
                            .description(article.getDescription())
                            .pubDate(article.getPubDate())
                            .imageUrl(article.getImageUrl())
//                            .likes(article.getLikes())
                            .commentCount(commentCount)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public ArticleDetail searchDetail(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() ->
                        new EntityNotFoundException("해당 ID의 기사를 찾을 수 없습니다: " + articleId)
                );

        Long commentCount = commentRepository.countByArticleId(articleId);

        return new ArticleDetail(
                article.getId(),
                article.getTitle(),
                article.getPubDate(),
                article.getContent(),
                article.getImageUrl(),
                article.getLikes(),
                commentCount
        );
    }

    /** 네이버 뉴스 본문과 이미지 추출 */
    private ArticleDetail crawlNaverArticle(NaverNewsResponse.Item item) {
        try {
            Document doc = Jsoup.connect(item.getLink())
                    .userAgent("Mozilla/5.0")
                    .timeout(5000)
                    .get();

            // 본문 영역 (뉴스판에 따라 두 가지 셀렉터를 준비해 둡니다)
            Element articleElemnt = doc.selectFirst("article#dic_area");
            Element titleElement = doc.selectFirst("h2#title_area");

            // 1) HTML 그대로 가져온 뒤 <br> 과 </p> 를 줄바꿈으로 바꿔줍니다.
            String html = articleElemnt != null ? articleElemnt.html() : "";
            String title = titleElement != null ? titleElement.html() : "";

            html = html.replaceAll("\n", "");

            String imgUrl = null;
            if (item != null) {
                // 1) <img> 태그 – src, data-src, data-lazy-src
                for (Element img : articleElemnt.select("img")) {
                    String url = null;
                    if (img.hasAttr("data-src")) {
                        url = img.absUrl("data-src");
                    }

                    if (url != null && !url.isEmpty()) {
                        imgUrl = url;
                        break;
                    }
                }
            }

            return new ArticleDetail(
                    0L,
                    title,
                    item.getPubDate(),
                    html,
                    imgUrl,
                    0L,
                    0L
            );
        } catch (IOException e) {
            // 실패 시 최소 정보만 반환
            return new ArticleDetail(
                    null, null, item.getPubDate(), null, null, 0L, 0L
            );
        }
    }

    public List<ArticleListResponse> searchByKeywordAndDate(String keyword, String date) {
        String cleanKeyword = keyword.trim().toLowerCase();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        System.out.println("keyword: " + keyword);
        return articleRepository.findAll().stream()
                .filter(article -> {
                    String plainTitle = Jsoup.parse(article.getTitle()).text().toLowerCase();
                    String plainDesc = Jsoup.parse(article.getDescription()).text().toLowerCase();
                    return plainTitle.contains(cleanKeyword) || plainDesc.contains(cleanKeyword);
                })
                .filter(article -> {
                    if (date == null || date.isBlank()) return true; // 날짜 미입력 시 통과
                    try {
                        ZonedDateTime parsedDate = ZonedDateTime.parse(article.getPubDate(), formatter);
                        String articleDate = parsedDate.toLocalDate().toString(); // yyyy-MM-dd
                        return articleDate.equals(date);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .map(article -> {
                    Long commentCount = commentRepository.countByArticleId(article.getId());
                    return ArticleListResponse.builder()
                            .id(article.getId())
                            .title(article.getTitle())
                            .link(article.getLink())
                            .description(article.getDescription())
                            .pubDate(article.getPubDate())
                            .imageUrl(article.getImageUrl())
//                            .likes(article.getLikes())
                            .commentCount(commentCount)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
