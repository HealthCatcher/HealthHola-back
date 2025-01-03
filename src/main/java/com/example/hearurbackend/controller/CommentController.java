package com.example.hearurbackend.controller;

import com.example.hearurbackend.dto.comment.CommentRequestDto;
import com.example.hearurbackend.dto.comment.CommentResponseDto;
import com.example.hearurbackend.dto.oauth.CustomOAuth2User;
import com.example.hearurbackend.entity.community.Comment;
import com.example.hearurbackend.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/community")
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "댓글 작성")
    @PostMapping("/post/{postNo}/comment")
    public ResponseEntity<Void> createComment(
            @PathVariable Long postNo,
            @RequestBody @Valid CommentRequestDto commentDTO,
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        Comment newComment = commentService.createComment(postNo, auth.getUsername(), commentDTO);
        String commentId = newComment.getId().toString();
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/community/post/{postNo}/comment/{commentId}")
                .buildAndExpand(postNo,commentId)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "댓글 수정")
    @PutMapping("/comment/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable UUID commentId,
            @RequestBody @Valid CommentRequestDto commentDTO,
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        commentService.updateComment(auth.getUsername(), commentId, commentDTO);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable UUID commentId,
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        commentService.deleteComment(commentId, auth.getUsername());
        return ResponseEntity.noContent().build();
    }
}
