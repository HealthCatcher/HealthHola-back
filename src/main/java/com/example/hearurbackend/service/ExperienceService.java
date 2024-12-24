package com.example.hearurbackend.service;

import com.example.hearurbackend.dto.experience.NoticeRequestDto;
import com.example.hearurbackend.dto.experience.NoticeResponseDto;
import com.example.hearurbackend.dto.review.ReviewResponseDto;
import com.example.hearurbackend.entity.experience.Notice;
import com.example.hearurbackend.entity.user.User;
import com.example.hearurbackend.repository.ExperienceNoticeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExperienceService {
    private final ExperienceNoticeRepository experienceNoticeRepository;
    private final UserService userService;
    public List<NoticeResponseDto> getNoticeList() {
        List<Notice> noticeEntities = experienceNoticeRepository.findAll();
        return noticeEntities.stream()
                .map(notice -> {
                    Optional<User> userOptional = userService.getUser(notice.getAuthor().getUsername());
                    String authorNickname = userOptional.map(User::getNickname).orElse("Unknown Author");

                    return NoticeResponseDto.builder()
                            .id(notice.getId())
                            .category(notice.getCategory())
                            .title(notice.getTitle())
                            .author(authorNickname)
                            .content(notice.getContent())
                            .createDate(notice.getCreateDate())
                            .startDate(notice.getStartDate())
                            .endDate(notice.getEndDate())
                            .build();
                })
                .collect(Collectors.toList()).reversed();
    }

    public NoticeResponseDto getNoticeDetail(UUID noticeId) {
        Notice notice = experienceNoticeRepository.findById(noticeId).orElseThrow(() -> new EntityNotFoundException("Notice not found with id: " + noticeId));
        notice.increaseViews();
        experienceNoticeRepository.save(notice);
        List<ReviewResponseDto> reviewDTOList = notice.getReviews().stream()
                .map(review -> {
                    Optional<User> userOptional = userService.getUser(notice.getAuthor().getUsername());
                    String authorNickname = userOptional.map(User::getNickname).orElse("Unknown Author");
                    return new ReviewResponseDto(
                            review.getId(),
                            authorNickname,
                            review.getContent(),
                            review.getCreatedAt()
                    );
                })
                .toList();
        return NoticeResponseDto.builder()
                .id(notice.getId())
                .category(notice.getCategory())
                .title(notice.getTitle())
                .content(notice.getContent())
                .author(notice.getAuthor().getUsername())
                .createDate(notice.getCreateDate())
                .startDate(notice.getStartDate())
                .endDate(notice.getEndDate())
                .reviews(reviewDTOList)
                .views(notice.getViews())
                .build();
    }

    public Notice createNotice(NoticeRequestDto noticeRequestDto, String username) {
        LocalDateTime now = LocalDateTime.now();
        Notice notice = Notice.builder()
                .title(noticeRequestDto.getTitle())
                .content(noticeRequestDto.getContent())
                .author(userService.getUser(username).orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username)))
                .createDate(now)
                .startDate(noticeRequestDto.getStartDate())
                .endDate(noticeRequestDto.getEndDate())
                .category(noticeRequestDto.getCategory())
                .campaignDetails(noticeRequestDto.getCampaignDetails())
                .instruction(noticeRequestDto.getInstruction())
                .build();
        return experienceNoticeRepository.save(notice);
    }

    public void updateNotice(UUID noticeId, NoticeRequestDto noticeRequestDTO, String username) {
        Notice notice = experienceNoticeRepository.findById(noticeId).orElseThrow(
                () -> new EntityNotFoundException("Post not found with id: " + noticeId));
        if (!notice.getAuthor().getUsername().equals(username)) {
            throw new SecurityException("You are not the author of this post.");
        }
        notice.updateNotice(noticeRequestDTO.getTitle(), noticeRequestDTO.getContent());
        experienceNoticeRepository.save(notice);
    }

    public void deleteNotice(UUID noticeId, String username) {
        Notice notice = experienceNoticeRepository.findById(noticeId).orElseThrow(
                () -> new EntityNotFoundException("Post not found with id: " + noticeId));
        if (!notice.getAuthor().getUsername().equals(username)) {
            throw new SecurityException("You are not the author of this post.");
        }
        experienceNoticeRepository.delete(notice);
    }

    public void applyNotice(UUID noticeId, String username) {
        Notice notice = experienceNoticeRepository.findById(noticeId).orElseThrow(
                () -> new EntityNotFoundException("Post not found with id: " + noticeId));
        User user = userService.getUser(username).orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
        notice.addParticipant(user);
        experienceNoticeRepository.save(notice);
    }

    public void cancelApplyNotice(UUID noticeId, String username) {
        Notice notice = experienceNoticeRepository.findById(noticeId).orElseThrow(
                () -> new EntityNotFoundException("Post not found with id: " + noticeId));
        User user = userService.getUser(username).orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
        notice.removeParticipant(user);
        experienceNoticeRepository.save(notice);
    }

    public List<String> getParticipants(UUID noticeId) {
        Notice notice = experienceNoticeRepository.findById(noticeId).orElseThrow(
                () -> new EntityNotFoundException("Post not found with id: " + noticeId));
        return notice.getParticipants().stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
    }
}
