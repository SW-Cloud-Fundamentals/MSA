package com.example.articleservice.Controller;

import com.example.articleservice.Code.ResponseCode;
import com.example.articleservice.Dto.Comment.CommentMessage;
import com.example.articleservice.Dto.Comment.RequestCommentDto;
import com.example.articleservice.Dto.Comment.ResponseCommentDto;
import com.example.articleservice.Dto.Response.ResponseDTO;
import com.example.articleservice.Service.CommentService;
import com.example.articleservice.messagequeue.KafkaProducer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final KafkaProducer kafkaProducer;

    // ✅ 특정 기사 ID에 대한 댓글 생성
    @PostMapping("/{articleId}")
    public ResponseEntity<ResponseDTO<ResponseCommentDto>> createComment(
            @PathVariable Long articleId,
            @Valid @RequestBody RequestCommentDto dto,
            @RequestHeader("Authorization") String authHeader) {
        dto.setArticleId(articleId);

        String token = authHeader.substring("Bearer ".length());

        ResponseCommentDto response = commentService.createComment(dto, token);

        CommentMessage commentMessage = CommentMessage.converter("create", response);
        /* send this order to the kafka */
        kafkaProducer.send("my_topic_comments", commentMessage);

        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_CREATE_COMMENT, response));
    }

    // ✅ 전체 댓글 조회
    @GetMapping
    public ResponseEntity<ResponseDTO<List<ResponseCommentDto>>> getAllComments() {
        List<ResponseCommentDto> response = commentService.getAllComments();
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_GET_COMMENT, response));
    }

    // ✅ 특정 기사 ID의 댓글 목록 조회
    @GetMapping("/{articleId}")
    public ResponseEntity<ResponseDTO<List<ResponseCommentDto>>> getCommentsByArticleId(@PathVariable Long articleId) {
        List<ResponseCommentDto> response = commentService.getCommentsByArticleId(articleId);
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_GET_COMMENT, response));
    }

    // ✅ 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<ResponseDTO<ResponseCommentDto>> updateComment(@PathVariable Long commentId,
                                                                         @RequestBody RequestCommentDto requestDto,
                                                                         @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring("Bearer ".length());

        ResponseCommentDto updatedComment = commentService.updateComment(commentId, requestDto, token);
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_UPDATE_COMMENT, updatedComment));
    }

    // ✅ 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ResponseDTO<Void>> deleteComment(@PathVariable Long commentId,
                                                           @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring("Bearer ".length());

        commentService.deleteComment(commentId, token);
        return ResponseEntity.status(ResponseCode.SUCCESS_COMMENT_DELETE.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_COMMENT_DELETE, null));
    }
}