package com.example.hearurbackend.controller;

import com.example.hearurbackend.dto.oauth.CustomOAuth2User;
import com.example.hearurbackend.dto.post.PostRequestDto;
import com.example.hearurbackend.dto.post.PostResponseDto;

import com.example.hearurbackend.entity.Post;
import com.example.hearurbackend.service.PostService;
import io.swagger.v3.oas.annotations.Operation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/community")
public class PostController {
    private final PostService postService;

    @Operation(summary = "게시글 목록 조회")
    @GetMapping("/post")
    public ResponseEntity<List<PostResponseDto>> getPostList() {
        List<PostResponseDto> postList = postService.getPostList();
        return ResponseEntity.ok(postList);
    }

    @Operation(summary = "게시글 상세 조회")
    @GetMapping("/post/{postNo}")
    public ResponseEntity<PostResponseDto> getPostDetail(
            @PathVariable Long postNo
    ) {
        PostResponseDto responseDTO = postService.getPostDetail(postNo);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "게시글 작성")
    @PostMapping("/post")
    public ResponseEntity<?> createPost(
            @AuthenticationPrincipal CustomOAuth2User auth,
            @RequestBody PostRequestDto postRequestDto
    ) {
        Post newPost = postService.createPost(postRequestDto, auth.getUsername());
        String postNo = newPost.getNo().toString();
        URI postUri = URI.create("/community/post/" + postNo);
        return ResponseEntity.created(postUri).build();
    }

    @Operation(summary = "게시글 수정")
    @PutMapping("/post/{postNo}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long postNo,
            @AuthenticationPrincipal CustomOAuth2User auth,
            @RequestBody PostRequestDto postRequestDto
    ) {
        postService.updatePost(postNo, postRequestDto, auth.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/post/{postNo}")
    public ResponseEntity<?> deletePost(
            @PathVariable Long postNo,
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        postService.deletePost(postNo, auth.getUsername());
        return ResponseEntity.noContent().build();
    }
}