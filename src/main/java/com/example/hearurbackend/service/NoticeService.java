package com.example.hearurbackend.service;

import com.example.hearurbackend.domain.UserRole;
import com.example.hearurbackend.dto.experience.NoticeRequestDto;
import com.example.hearurbackend.dto.experience.NoticeResponseDto;
import com.example.hearurbackend.dto.oauth.CustomOAuth2User;
import com.example.hearurbackend.dto.review.ReviewResponseDto;
import com.example.hearurbackend.entity.experience.Notice;
import com.example.hearurbackend.entity.experience.ParticipantEntry;
import com.example.hearurbackend.entity.user.User;
import com.example.hearurbackend.repository.ExperienceNoticeRepository;
import com.example.hearurbackend.repository.ParticipantEntryRepository;
import com.example.hearurbackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {
    private final ExperienceNoticeRepository experienceNoticeRepository;
    private final UserService userService;
    private final ParticipantEntryRepository participantEntryRepository;
    private final UserRepository userRepository;
    public List<NoticeResponseDto> getNoticeList(CustomOAuth2User auth) {
        List<Notice> noticeEntities = experienceNoticeRepository.findAll();
        return noticeEntities.stream()
                .map(notice -> {
                    Optional<User> userOptional = userService.getUser(notice.getAuthor().getUsername());
                    String authorNickname = userOptional.map(User::getNickname).orElse("Unknown Author");
                    if(auth==null){
                        return NoticeResponseDto.builder()
                                .id(notice.getId())
                                .category(notice.getCategory())
                                .title(notice.getTitle())
                                .author(authorNickname)
                                .content(notice.getContent())
                                .createDate(notice.getCreateDate())
                                .startDate(notice.getStartDate())
                                .endDate(notice.getEndDate())
                                .views(notice.getViews())
                                .maxParticipants(notice.getMaxParticipants())
                                .participants(notice.getParticipantEntries().size())
                                .favoriteCount(notice.getFavoritesCount())
                                .build();
                    }
                    User user = userService.getUser(auth.getUsername()).orElse(null);
                    boolean isLiked = user != null && user.getFavoriteNotices().contains(notice);
                    return NoticeResponseDto.builder()
                            .id(notice.getId())
                            .category(notice.getCategory())
                            .title(notice.getTitle())
                            .author(authorNickname)
                            .content(notice.getContent())
                            .createDate(notice.getCreateDate())
                            .startDate(notice.getStartDate())
                            .endDate(notice.getEndDate())
                            .views(notice.getViews())
                            .maxParticipants(notice.getMaxParticipants())
                            .participants(notice.getParticipantEntries().size())
                            .favoriteCount(notice.getFavoritesCount())
                            .isFavorite(isLiked)
                            .titleImageUrls(notice.getTitleImageUrl())
                            .detailImageUrls(notice.getDetailImageUrls())
                            .build();
                })
                .collect(Collectors.toList()).reversed();
    }

    public NoticeResponseDto getNoticeDetail(UUID noticeId, CustomOAuth2User auth) {
        Notice notice = experienceNoticeRepository.findById(noticeId).orElseThrow(() -> new EntityNotFoundException("Notice not found with id: " + noticeId));
        notice.increaseViews();
        experienceNoticeRepository.save(notice);
        List<ReviewResponseDto> reviewDTOList = notice.getReviews().stream()
                .map(review -> new ReviewResponseDto(
                        review.getId(),
                        review.getUser().getNickname(),
                        review.getContent(),
                        review.getCreatedAt(),
                        review.getUrls()
                ))
                .toList();
        if(auth==null){
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
                    .participants(notice.getParticipantEntries().size())
                    .maxParticipants(notice.getMaxParticipants())
                    .campaignDetails(notice.getCampaignDetails())
                    .instruction(notice.getInstruction())
                    .favoriteCount(notice.getFavoritesCount())
                    .titleImageUrls(notice.getTitleImageUrl())
                    .detailImageUrls(notice.getDetailImageUrls())
                    .build();
        }
        boolean isFavorite = userService.getUser(auth.getUsername()).map(user -> user.getFavoriteNotices().contains(notice)).orElse(false);
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
                .participants(notice.getParticipantEntries().size())
                .maxParticipants(notice.getMaxParticipants())
                .campaignDetails(notice.getCampaignDetails())
                .instruction(notice.getInstruction())
                .favoriteCount(notice.getFavoritesCount())
                .isFavorite(isFavorite)
                .titleImageUrls(notice.getTitleImageUrl())
                .detailImageUrls(notice.getDetailImageUrls())
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
                .maxParticipants(noticeRequestDto.getMaxParticipants())
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
        Notice notice = experienceNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + noticeId));
        User user = userService.getUser(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));

        ParticipantEntry participantEntry = participantEntryRepository.findByNoticeAndUser(notice, user)
                .orElse(new ParticipantEntry(notice, user));

//        try {
//            if (!isEligibleForNewEntry(participantEntry, user)) {
//                throw new IllegalStateException("You have already applied for this notice today.");
//            }
//        } catch (IllegalStateException e) {
//            throw new IllegalStateException(e.getMessage() + " " + getTimeUntilNextEntry(participantEntry, user));
//        }

        participantEntry.increaseEntryCount();
        participantEntryRepository.save(participantEntry);
    }

    private boolean isEligibleForNewEntry(ParticipantEntry participantEntry, User user) {
        LocalDateTime lastEntryTime = participantEntry.getLastEntryDate();
        LocalDateTime now = LocalDateTime.now();
        // 먼저 날짜가 변경되었는지 확인
        if (!lastEntryTime.toLocalDate().equals(now.toLocalDate())) {
            return true;
        }
        // 같은 날이라면, 프리미엄 사용자의 경우에는 1시간 간격 확인
        if (user.getRole() == UserRole.ROLE_PREMIUM) {
            return Duration.between(lastEntryTime, now).toHours() >= 1;
        }
        // 일반 사용자는 같은 날 다시 응모할 수 없음
        return false;
    }

    private String getTimeUntilNextEntry(ParticipantEntry participantEntry, User user) {
        LocalDateTime lastEntryTime = participantEntry.getLastEntryDate();

        // 다음날 자정
        LocalDateTime nextDayMidnight = lastEntryTime.toLocalDate().atStartOfDay().plusDays(1);
        LocalDateTime nextAvailableTime;

        if (user.getRole() == UserRole.ROLE_PREMIUM) {
            LocalDateTime oneHourAfterLastEntry = lastEntryTime.plusHours(1);
            nextAvailableTime = oneHourAfterLastEntry.isBefore(nextDayMidnight) ? oneHourAfterLastEntry : nextDayMidnight;
        } else {
            nextAvailableTime = nextDayMidnight;
        }
        return formatNextAvailableTime(nextAvailableTime);
    }

    private String formatNextAvailableTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return dateTime.format(formatter);
    }



    public void cancelApplyNotice(UUID noticeId, String username) {
        Notice notice = experienceNoticeRepository.findById(noticeId).orElseThrow(
                () -> new EntityNotFoundException("Post not found with id: " + noticeId));
        User user = userService.getUser(username).orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
        ParticipantEntry participantEntry = participantEntryRepository.findByNoticeAndUser(notice, user).orElseThrow(() -> new EntityNotFoundException("Participant entry not found"));
        participantEntryRepository.delete(participantEntry);
    }

    public List<String> getParticipants(UUID noticeId) {
        Notice notice = experienceNoticeRepository.findById(noticeId).orElseThrow(
                () -> new EntityNotFoundException("Post not found with id: " + noticeId));
        return notice.getParticipantEntries().stream()
                .map(participantEntry -> participantEntry.getUser().getUsername())
                .collect(Collectors.toList());
    }

    public Notice getNotice(UUID noticeId) {
        return experienceNoticeRepository.findById(noticeId).orElseThrow(
                () -> new EntityNotFoundException("Post not found with id: " + noticeId));
    }

    public void uploadImage(UUID noticeId, String fileUrl, int index) {
        Notice notice = experienceNoticeRepository.findById(noticeId).orElseThrow(
                () -> new EntityNotFoundException("Notice not found with id: " + noticeId));
        switch (index) {
            case 0:
                notice.addTitleImageUrl(fileUrl);
                break;
            case 1:
                notice.addDetailImageUrl(fileUrl);
                break;
            default:
                throw new IllegalArgumentException("Invalid index: " + index);
        }
        experienceNoticeRepository.save(notice);
    }

    public void favoriteNotice(UUID noticeId, String username) {
        Notice notice = experienceNoticeRepository.findById(noticeId).orElseThrow(
                () -> new EntityNotFoundException("Post not found with id: " + noticeId));
        User user = userService.getUser(username).orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));

        user.addFavoriteNotice(notice); // User 객체의 메소드를 사용하여 Notice를 추가함으로써 양방향 동기화 처리
        experienceNoticeRepository.save(notice);
        userRepository.save(user); // 변경사항을 User에도 반영
    }
}
