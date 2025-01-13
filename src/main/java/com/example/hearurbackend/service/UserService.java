package com.example.hearurbackend.service;

import com.example.hearurbackend.dto.experience.NoticeResponseDto;
import com.example.hearurbackend.dto.user.AddressDto;
import com.example.hearurbackend.dto.user.UserDto;
import com.example.hearurbackend.entity.experience.Notice;
import com.example.hearurbackend.entity.user.Address;
import com.example.hearurbackend.entity.user.User;
import com.example.hearurbackend.repository.AddressRepository;
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
    private final AddressRepository addressRepository;

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
    public void changeAddress(String username, AddressDto addressRequestDto) {
        // 사용자 조회
        User user = userRepository.findById(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 사용자의 현재 주소를 가져옴
        Address address = user.getAddress();

        if (address == null) {
            address = new Address(user, addressRequestDto);
        } else {
            address.update(addressRequestDto);
        }
        addressRepository.save(address);
    }

    public AddressDto getAddress(String username) {
        User user = userRepository.findById(username).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Address address = user.getAddress();
        if (address == null) {
            return null;
        }
        return new AddressDto(address.getAddress(), address.getDetailAddress(), address.getZoneCode());
    }
}
