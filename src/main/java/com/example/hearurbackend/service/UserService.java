package com.example.hearurbackend.service;

import com.example.hearurbackend.dto.experience.NoticeResponseDto;
import com.example.hearurbackend.entity.experience.Notice;
import com.example.hearurbackend.entity.user.User;
import com.example.hearurbackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Optional<User> getUser(String username) {
        return userRepository.findById(username);
    }

    @Transactional
    public void changeNickname(String username, String nickname) {
        User user = userRepository.findById(username).orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.changeNickname(nickname);
        userRepository.save(user);
    }

    public List<NoticeResponseDto> getFavoriteNoticeList(String username) {
        User user = userRepository.findById(username).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return user.getFavoriteNotices().stream()
                .map(notice -> {
                    Optional<User> userOptional = getUser(notice.getAuthor().getUsername());
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
                            .views(notice.getViews())
                            .maxParticipants(notice.getMaxParticipants())
                            .participants(notice.getParticipantEntries().size())
                            .favoriteCount(notice.getFavoritesCount())
                            .build();
                })
                .collect(Collectors.toList()).reversed();
    }

    public boolean isUserAdmin(String username) {
        User user = userRepository.findById(username).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return user.isAdmin();
    }

    public List<NoticeResponseDto> getAppliedNoticeList(String username) {
        User user = userRepository.findById(username).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return user.getParticipatedExperiences().stream()
                .map(participantEntry -> {
                    Notice myNotice = participantEntry.getNotice();
                    Optional<User> userOptional = getUser(myNotice.getAuthor().getUsername());
                    String authorNickname = userOptional.map(User::getNickname).orElse("Unknown Author");
                    return NoticeResponseDto.builder()
                            .id(myNotice.getId())
                            .category(myNotice.getCategory())
                            .title(myNotice.getTitle())
                            .author(authorNickname)
                            .content(myNotice.getContent())
                            .createDate(myNotice.getCreateDate())
                            .startDate(myNotice.getStartDate())
                            .endDate(myNotice.getEndDate())
                            .views(myNotice.getViews())
                            .maxParticipants(myNotice.getMaxParticipants())
                            .participants(myNotice.getParticipantEntries().size())
                            .favoriteCount(myNotice.getFavoritesCount())
                            .build();
                })
                .collect(Collectors.toList()).reversed();
    }

    @Transactional
    public void changeAddress(String username, String address) {
        User user = userRepository.findById(username).orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.changeAddress(address);
        userRepository.save(user);
    }
}
