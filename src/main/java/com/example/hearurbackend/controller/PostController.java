package com.example.hearurbackend.controller;

import com.example.hearurbackend.dto.oauth.CustomOAuth2User;
import com.example.hearurbackend.dto.post.PostRequestDto;
import com.example.hearurbackend.dto.post.PostResponseDto;

import com.example.hearurbackend.entity.community.Post;
import com.example.hearurbackend.service.PostService;
import com.example.hearurbackend.service.S3Uploader;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/community")
public class PostController {
    private final PostService postService;
    private final S3Uploader s3Uploader;

    @Operation(summary = "게시글 목록 조회")
    @GetMapping("/post")
    public ResponseEntity<List<PostResponseDto>> getPostList(
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        List<PostResponseDto> postList = postService.getPostList(auth);
        return ResponseEntity.ok(postList);
    }

    @Operation(summary = "게시글 상세 조회")
    @GetMapping("/post/{postNo}")
    public ResponseEntity<PostResponseDto> getPostDetail(
            @PathVariable Long postNo,
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        PostResponseDto responseDTO = postService.getPostDetail(postNo, auth);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "게시글 작성")
    @PostMapping(value = "/post")
    public ResponseEntity<Void> createPost(
            @AuthenticationPrincipal CustomOAuth2User auth,
            @RequestBody PostRequestDto postRequestDto
    ) {

        Post newPost = postService.createPost(postRequestDto, auth.getUsername());
        String postNo = newPost.getNo().toString();
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/community/{postNo}")
                .buildAndExpand(postNo)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "게시글 수정")
    @PutMapping("/post/{postNo}")
    public ResponseEntity<Void> updatePost(
            @PathVariable Long postNo,
            @AuthenticationPrincipal CustomOAuth2User auth,
            @RequestBody PostRequestDto postRequestDto
    ) {
        postService.updatePost(postNo, postRequestDto, auth.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/post/{postNo}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postNo,
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        postService.deletePost(postNo, auth.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "게시글 좋아요 추가")
    @PostMapping("/post/{postNo}/like")
    public ResponseEntity<Void> likePost(
            @PathVariable Long postNo,
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        postService.addLike(postNo, auth.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "게시글 좋아요 취소")
    @DeleteMapping("/post/{postNo}/like")
    public ResponseEntity<Void> unlikePost(
            @PathVariable Long postNo,
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        postService.removeLike(postNo, auth.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "게시글 이미지 업로드")
    @PostMapping(value = "/post/{postNo}/image", consumes = "multipart/form-data")
    public ResponseEntity<Void> uploadImage(
            @PathVariable Long postNo,
            @RequestParam(value = "image", required = true) MultipartFile imageFile
    ) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            // 파일 저장 로직 실행
            String fileUrl = s3Uploader.upload(imageFile, "HealthHola-Post-Image/"+postNo.toString());
            postService.uploadImage(postNo, fileUrl);

        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "첫번째 Post인지 확인")
    @GetMapping("/post/first")
    public ResponseEntity<Boolean> isFirstPost(
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        return ResponseEntity.ok(postService.isFirstPost(auth.getUsername()));
    }
}