package com.example.hearurbackend.domain.user.service;

import com.example.hearurbackend.domain.experience.dto.NoticeResponseDto;
import com.example.hearurbackend.domain.user.dto.AddressDto;
import com.example.hearurbackend.domain.experience.entity.Notice;
import com.example.hearurbackend.domain.user.dto.BlockUserDto;
import com.example.hearurbackend.domain.user.entity.Address;
import com.example.hearurbackend.domain.user.entity.User;
import com.example.hearurbackend.domain.user.repository.AddressRepository;
import com.example.hearurbackend.domain.user.repository.UserRepository;
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
                            .isFavorite(true)
                            .titleImageUrls(notice.getTitleImageUrl())
                            .detailImageUrls(notice.getDetailImageUrls())
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
                            .isFavorite(user.getFavoriteNotices().contains(myNotice))
                            .titleImageUrls(myNotice.getTitleImageUrl())
                            .detailImageUrls(myNotice.getDetailImageUrls())
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

    @Transactional
    public void blockUser(String me, String you) {
        log.info("me: {}, you: {}", me, you);
        User meUser = userRepository.findById(me).orElseThrow(() -> new EntityNotFoundException("User not found"));
        User youUser = userRepository.findById(you).orElseThrow(() -> new EntityNotFoundException("User not found"));
        meUser.blockUser(youUser);
        userRepository.save(meUser);
    }

    public void unblockUser(String username, String username1) {
        User meUser = userRepository.findById(username).orElseThrow(() -> new EntityNotFoundException("User not found"));
        User youUser = userRepository.findById(username1).orElseThrow(() -> new EntityNotFoundException("User not found"));
        meUser.unblockUser(youUser);
        userRepository.save(meUser);
    }

    public List<BlockUserDto> getBlockedUserList(String username) {
        User user = userRepository.findById(username).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return user.getBlocks().stream()
                .map(block -> new BlockUserDto(block.getBlocked().getUsername()))
                .collect(Collectors.toList());
    }
}
