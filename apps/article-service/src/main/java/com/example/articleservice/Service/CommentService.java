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
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final JWTUtil jwtUtil;
    private final RankService rankService;

    // ✅ 댓글 생성
    public ResponseCommentDto createComment(RequestCommentDto requestDto,
                                            String token) {

        if (requestDto.getContent() == null || requestDto.getContent().trim().isEmpty()) {
            throw new CustomException(ErrorCode.CONTENT_EMPTY);
        }

        if (requestDto.getContent().length() > 200) {
            throw new CustomException(ErrorCode.COMMENT_CONTENT_OUTNUMBER);
        }

        String role = jwtUtil.getRole(token);
        String username = jwtUtil.getUsername(token);

        Article article = articleRepository.findById(requestDto.getArticleId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Comment comment = Comment.fromRequestDto(requestDto, username, role, article);

        rankService.updateScore(requestDto.getArticleId(), 0, +1);

        try {
            Comment saved = commentRepository.save(comment);
            article.setCommentCount(article.getCommentCount() + 1);
            return ResponseCommentDto.dto(saved);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ 전체 댓글 조회
    public List<ResponseCommentDto> getAllComments() {
        return commentRepository.findAll().stream()
                .map(ResponseCommentDto::dto)
                .collect(Collectors.toList());
    }

    // ✅ 기사 ID로 댓글 조회
    public List<ResponseCommentDto> getCommentsByArticleId(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        List<Comment> comments = commentRepository.findByArticleId(article);
        return comments.stream()
                .map(ResponseCommentDto::dto)
                .collect(Collectors.toList());
    }

    // ✅ 댓글 수정
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

        Comment updated = commentRepository.save(comment);

        return ResponseCommentDto.dto(updated);
    }

    // ✅ 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, String token) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        String username = jwtUtil.getUsername(token);

        if (!comment.getUsername().equals(username)) {
            throw new CustomException(ErrorCode.USER_NOT_OWNER);
        }

        commentRepository.deleteById(commentId);
        Article article = comment.getArticleId();
        article.setCommentCount(Math.max(0, article.getCommentCount() - 1));

//        rankService.updateScore(requestDto.getArticleId(), 0, -1);
    }
}
