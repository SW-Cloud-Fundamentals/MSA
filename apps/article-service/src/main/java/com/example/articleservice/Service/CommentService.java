package com.example.articleservice.Service;

import com.example.articleservice.Code.ErrorCode;
import com.example.articleservice.Dto.Comment.RequestCommentDto;
import com.example.articleservice.Dto.Comment.ResponseCommentDto;
import com.example.articleservice.Exception.CustomException;
import com.example.articleservice.Model.Article;
import com.example.articleservice.Model.Comment;
import com.example.articleservice.Repository.ArticleRepository;
import com.example.articleservice.Repository.CommentRepository;
import com.example.jwt_util.util.JWTUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final JWTUtil jwtUtil;
    private final RankService rankService;

    /**
     * 1️⃣ 댓글 생성
     * @param requestDto 댓글 작성 요청 DTO
     * @param token      Authorization 헤더에서 추출한 JWT(“Bearer ” 제거 후 값)
     * @return ResponseCommentDto 저장된 댓글 정보 DTO
     */
    public ResponseCommentDto createComment(RequestCommentDto requestDto,
                                            String token) {

        if (requestDto.getContent() == null || requestDto.getContent().trim().isEmpty()) {
            throw new CustomException(ErrorCode.CONTENT_EMPTY);
        }

        // 200자 이상이면 ❌
        if (requestDto.getContent().length() > 200) {
            throw new CustomException(ErrorCode.COMMENT_CONTENT_OUTNUMBER);
        }

        String role = jwtUtil.getRole(token);
        String username = jwtUtil.getUsername(token);
        String nickname = jwtUtil.getNickname(token);

        Article article = articleRepository.findById(requestDto.getArticleId())
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));

        Comment comment = Comment.fromRequestDto(requestDto, username, nickname, role, article);
        comment.updateEvent("CREATE");  // Kafka에 보낼 eventType 설정
        // 랭킹 시스템에 댓글 추가 전달
        rankService.updateScore(requestDto.getArticleId(), 0, +1);

        try {
            Comment saved = commentRepository.save(comment);
            article.setCommentCount(article.getCommentCount() + 1);
            return ResponseCommentDto.dto(saved);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /** 2️⃣ 전체 댓글 조회 */
    public List<ResponseCommentDto> getAllComments() {
        return commentRepository.findAll().stream()
                .map(ResponseCommentDto::dto)
                .collect(Collectors.toList());
    }

    /** 3️⃣ 기사 ID로 댓글 조회 */
    public List<ResponseCommentDto> getCommentsByArticleId(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));
        // Comment(Entity)의 필드값인 deleted가 False인 댓글만 찾아옴
        List<Comment> comments = commentRepository.findByArticleIdAndDeletedFalse(article);
        return comments.stream()
                .map(ResponseCommentDto::dto)
                .collect(Collectors.toList());
    }

    /**
     * 4️⃣ 댓글 수정
     * – 작성자 본인인지 검증 후 내용 수정
     * – Kafka 이벤트 타입을 UPDATE 로 지정
     */
    @Transactional
    public ResponseCommentDto updateComment(Long commentId, RequestCommentDto requestDto,
                                            String token) {

        String username = jwtUtil.getUsername(token);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUsername().equals(username)) {
            throw new CustomException(ErrorCode.USER_NOT_OWNER);
        }

        if (requestDto.getContent() == null || requestDto.getContent().trim().isEmpty()) {
            throw new CustomException(ErrorCode.CONTENT_EMPTY);
        }

        comment.setContent(requestDto.getContent());
        comment.updateEvent("UPDATE");  // Kafka에 보낼 eventType 설정

        Comment updated = commentRepository.save(comment);

        return ResponseCommentDto.dto(updated);
    }

    /**
     * 5️⃣ 댓글 삭제 (Soft‑delete) 패턴 사용
     * – 실제 레코드는 지우지 않고 deleted=true로 표기
     * – 배치 스케줄러가 주기적으로 물리 삭제 수행
     * – 랭킹 점수 ‑1 반영
     */
    @Transactional
    public ResponseCommentDto deleteComment(Long commentId, String token) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        String username = jwtUtil.getUsername(token);

        if (!comment.getUsername().equals(username)) {
            throw new CustomException(ErrorCode.USER_NOT_OWNER);
        }

        comment.updateEvent("DELETE");  // Kafka에 보낼 eventType 설정
        /*
         * 삭제될 댓글이라는 것을 명시하기 위해 deleted 필드 true로 설정
         * 스케쥴러에서 deleted 필드가 true인 댓글들 일괄적으로 삭제
         */
        comment.setDeleted(true);

        Article article = comment.getArticleId();
        article.setCommentCount(Math.max(0, article.getCommentCount() - 1));

        rankService.updateScore(article.getId(), 0, -1);    // 랭킹 시스템에 댓글 삭제 전달
        return ResponseCommentDto.dto(comment);
    }
}
