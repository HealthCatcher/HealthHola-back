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
        List<Notice> notices = experienceNoticeRepository.findAll();
        return notices.stream().map(notice -> convertToDto(notice, auth)).collect(Collectors.toList());
    }

    public NoticeResponseDto getNoticeDetail(UUID noticeId, CustomOAuth2User auth) {
        Notice notice = experienceNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new EntityNotFoundException("Notice not found with id: " + noticeId));
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

        User user = auth != null ? userService.getUser(auth.getUsername()).orElse(null) : null;
        ParticipantEntry participantEntry = user != null ? participantEntryRepository.findByNoticeAndUser(notice, user).orElse(null) : null;

        String nextApplyTime = participantEntry != null ? getTimeUntilNextEntry(participantEntry, user) : null;
        return convertToDto(notice, auth, nextApplyTime, reviewDTOList);
    }

    private NoticeResponseDto convertToDto(Notice notice, CustomOAuth2User auth) {
        return convertToDto(notice, auth, null, null);
    }

    private NoticeResponseDto convertToDto(Notice notice, CustomOAuth2User auth, String nextApplyTime, List<ReviewResponseDto> reviewDTOList) {
        String authorNickname = userService.getUser(notice.getAuthor().getUsername())
                .map(User::getNickname).orElse("Unknown Author");
        boolean isLiked = Optional.ofNullable(auth)
                .flatMap(a -> userService.getUser(a.getUsername()))
                .map(user -> user.getFavoriteNotices().contains(notice))
                .orElse(false);

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
                .campaignDetails(notice.getCampaignDetails())
                .instruction(notice.getInstruction())
                .titleImageUrls(notice.getTitleImageUrl())
                .detailImageUrls(notice.getDetailImageUrls())
                .reviews(reviewDTOList)
                .nextApplyTime(nextApplyTime)
                .build();
    }

    public Notice createNotice(NoticeRequestDto noticeRequestDto, String username) {
        User author = userService.getUser(username).orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
        Notice notice = new Notice(noticeRequestDto, author, LocalDateTime.now());
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

        Optional<ParticipantEntry> existingParticipantEntry = participantEntryRepository.findByNoticeAndUser(notice, user);
        ParticipantEntry participantEntry;
        if (existingParticipantEntry.isEmpty()) {
            participantEntry = new ParticipantEntry(notice, user);
        } else {
            participantEntry = existingParticipantEntry.get();
            if (!isEligibleForNewEntry(participantEntry, user)) {
                String message = "You have already applied for this notice today. " + getTimeUntilNextEntry(participantEntry, user);
                throw new IllegalStateException(message);
            }
        }
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

public void cancelFavoriteNotice(UUID noticeId, String username) {
    Notice notice = experienceNoticeRepository.findById(noticeId).orElseThrow(
            () -> new EntityNotFoundException("Post not found with id: " + noticeId));
    User user = userService.getUser(username).orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));

    user.removeFavoriteNotice(notice); // User 객체의 메소드를 사용하여 Notice를 제거함으로써 양방향 동기화 처리
    experienceNoticeRepository.save(notice);
    userRepository.save(user); // 변경사항을 User에도 반영
}
}
