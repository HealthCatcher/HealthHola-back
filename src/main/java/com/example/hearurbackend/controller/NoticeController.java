package com.example.hearurbackend.controller;

import com.example.hearurbackend.dto.experience.NoticeRequestDto;
import com.example.hearurbackend.dto.experience.NoticeResponseDto;
import com.example.hearurbackend.dto.oauth.CustomOAuth2User;
import com.example.hearurbackend.entity.experience.Notice;
import com.example.hearurbackend.service.NoticeService;
import com.example.hearurbackend.service.S3Uploader;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/experience")
public class NoticeController {
    private final NoticeService noticeService;
    private final S3Uploader s3Uploader;

    @Operation(summary = "체험단 공고 목록 조회")
    @GetMapping("/notice")
    public ResponseEntity<List<NoticeResponseDto>> getNoticeList(
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        List<NoticeResponseDto> noticeList = noticeService.getNoticeList(auth);
        return ResponseEntity.ok(noticeList);
    }

    @Operation(summary = "체험단 공고 상세 조회")
    @GetMapping("/notice/{noticeId}")
    public ResponseEntity<NoticeResponseDto> getNoticeDetail(
            @PathVariable UUID noticeId,
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        NoticeResponseDto responseDTO = noticeService.getNoticeDetail(noticeId,auth);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "체험단 공고 작성")
    @PostMapping(value= "/notice")
    public ResponseEntity<Void> createPost(
            @AuthenticationPrincipal CustomOAuth2User auth,
            @RequestBody NoticeRequestDto noticeRequestDto
    ) {
        Notice newNotice = noticeService.createNotice(noticeRequestDto, auth.getUsername());
        String noticeId = newNotice.getId().toString();
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{noticeId}")
                .buildAndExpand(noticeId)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "체험단 공고 수정")
    @PutMapping("/notice/{noticeId}")
    public ResponseEntity<Void> updatePost(
            @PathVariable UUID noticeId,
            @AuthenticationPrincipal CustomOAuth2User auth,
            @RequestBody NoticeRequestDto noticeRequestDto
    ) {
        noticeService.updateNotice(noticeId, noticeRequestDto, auth.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "체험단 공고 삭제")
    @DeleteMapping("/notice/{noticeId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable UUID noticeId,
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        noticeService.deleteNotice(noticeId, auth.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "체험단 참가 신청")
    @PostMapping("/notice/{noticeId}/apply")
    public ResponseEntity<Void> applyNotice(
            @PathVariable UUID noticeId,
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        noticeService.applyNotice(noticeId, auth.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "체험단 참가 신청 취소")
    @DeleteMapping("/notice/{noticeId}/apply")
    public ResponseEntity<Void> cancelApplyNotice(
            @PathVariable UUID noticeId,
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        noticeService.cancelApplyNotice(noticeId, auth.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "체험단 참여자 목록 조회")
    @GetMapping("/notice/{noticeId}/participants")
    public ResponseEntity<List<String>> getParticipants(
            @PathVariable UUID noticeId
    ) {
        List<String> participants = noticeService.getParticipants(noticeId);
        return ResponseEntity.ok(participants);
    }

    @Operation(summary = "체험단 대표 이미지 업로드")
    @PostMapping(value="/notice/{noticeId}/title-image", consumes = "multipart/form-data")
    public ResponseEntity<Void> uploadTitleImage(
            @PathVariable UUID noticeId,
            @RequestParam("image") MultipartFile imageFile
    ) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            // 파일 저장 로직 실행
            String folderPath = "HealthHola-Notice-Image/" + noticeId.toString() + "/title";
            String fileUrl = s3Uploader.upload(imageFile, folderPath);
            noticeService.uploadImage(noticeId, fileUrl, 0);
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "체험단 상세 이미지 업로드")
    @PostMapping(value="/notice/{noticeId}/detail-image", consumes = "multipart/form-data")
    public ResponseEntity<Void> uploadDetailImage(
            @PathVariable UUID noticeId,
            @RequestParam("image") MultipartFile imageFile
    ) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            // 파일 저장 로직 실행
            String fileUrl = s3Uploader.upload(imageFile, "HealthHola-Notice-Image/"+noticeId.toString()+"/details"); // 예시 함수, 파일을 저장하고 파일 이름을 반환
            noticeService.uploadImage(noticeId, fileUrl, 1);
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "체험단 공고 찜")
    @PostMapping("/notice/{noticeId}/favorite")
    public ResponseEntity<Void> favoriteNotice(
            @PathVariable UUID noticeId,
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        noticeService.favoriteNotice(noticeId, auth.getUsername());
        return ResponseEntity.noContent().build();
    }
}
