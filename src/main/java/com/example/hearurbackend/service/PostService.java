package com.example.hearurbackend.service;

import com.example.hearurbackend.dto.comment.CommentResponseDto;
import com.example.hearurbackend.dto.oauth.CustomOAuth2User;
import com.example.hearurbackend.dto.post.PostRequestDto;
import com.example.hearurbackend.dto.post.PostResponseDto;
import com.example.hearurbackend.entity.community.Like;
import com.example.hearurbackend.entity.community.Post;
import com.example.hearurbackend.entity.user.User;
import com.example.hearurbackend.repository.LikeRepository;
import com.example.hearurbackend.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;
    private final LikeRepository likeRepository;

    public List<PostResponseDto> getPostList(CustomOAuth2User auth) {
        List<Post> postEntities = postRepository.findAll();
        return postEntities.stream()
                .map(post -> {
                    Optional<User> userOptional = userService.getUser(post.getAuthor());
                    String authorNickname = userOptional.map(User::getNickname).orElse("Unknown Author");
                    if(auth==null){
                        return PostResponseDto.builder()
                                .no(post.getNo())
                                .category(post.getCategory())
                                .title(post.getTitle())
                                .author(authorNickname)
                                .createDate(post.getCreateDate())
                                .views(post.getViews())
                                .likes(post.getLikesCount())
                                .content(post.getContent())
                                .commentsCount(post.getCommentsCount())
                                .imageUrl(post.getImageUrl())
                                .build();
                    }
                    User user = userService.getUser(auth.getUsername()).orElse(null);
                    boolean isLiked = user != null && likeRepository.existsByUserAndPost(user, post);
                    return PostResponseDto.builder()
                            .no(post.getNo())
                            .category(post.getCategory())
                            .title(post.getTitle())
                            .author(authorNickname)
                            .createDate(post.getCreateDate())
                            .views(post.getViews())
                            .likes(post.getLikesCount())
                            .content(post.getContent())
                            .commentsCount(post.getCommentsCount())
                            .isLiked(isLiked)
                            .imageUrl(post.getImageUrl())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public PostResponseDto getPostDetail(Long postNo, CustomOAuth2User auth) {
        Post post = postRepository.findById(postNo).orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postNo));
        post.increaseViews();
        postRepository.save(post);
        List<CommentResponseDto> commentDTOList = post.getComments().stream()
                .map(comment -> {
                    Optional<User> userOptional = userService.getUser(comment.getAuthor());
                    String authorNickname = userOptional.map(User::getNickname).orElse("Unknown Author");
                    return new CommentResponseDto(
                            comment.getId(),
                            authorNickname,
                            comment.getContent(),
                            comment.getCreateDate(),
                            comment.isUpdated());
                })
                .toList();

        Optional<User> userOptional = userService.getUser(post.getAuthor());
        String authorNickname = userOptional.map(User::getNickname).orElse("Unknown Author");
        if(auth==null){
            return PostResponseDto.builder()
                    .no(post.getNo())
                    .category(post.getCategory())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .author(authorNickname)
                    .createDate(post.getCreateDate())
                    .updateDate(post.getUpdateDate())
                    .isUpdated(post.isUpdated())
                    .comments(commentDTOList)
                    .views(post.getViews())
                    .likes(post.getLikesCount())
                    .imageUrl(post.getImageUrl())
                    .build();
        }
        User user = userService.getUser(auth.getUsername()).orElse(null);
        boolean isLiked = user != null && likeRepository.existsByUserAndPost(user, post);
        return PostResponseDto.builder()
                .no(post.getNo())
                .category(post.getCategory())
                .title(post.getTitle())
                .content(post.getContent())
                .author(authorNickname)
                .createDate(post.getCreateDate())
                .updateDate(post.getUpdateDate())
                .isUpdated(post.isUpdated())
                .comments(commentDTOList)
                .views(post.getViews())
                .likes(post.getLikesCount())
                .isLiked(isLiked)
                .imageUrl(post.getImageUrl())
                .build();
    }

    @Transactional
    public Post createPost(PostRequestDto postDTO, String username) {
        LocalDateTime now = LocalDateTime.now();
        Post post = Post.builder()
                .title(postDTO.getTitle())
                .category(postDTO.getCategory())
                .content(postDTO.getContent())
                .author(username)
                .createDate(now)
                .updateDate(now)
                .isUpdated(false)
                .build();
        return postRepository.save(post);
    }

    @Transactional
    public void updatePost(Long postNo, PostRequestDto postDTO, String username) {
        Post post = postRepository.findById(postNo).orElseThrow(
                () -> new EntityNotFoundException("Post not found with id: " + postNo));
        if (!post.getAuthor().equals(username)) {
            throw new SecurityException("You are not the author of this post.");
        }
        post.updatePost(postDTO.getTitle(), postDTO.getContent());
        postRepository.save(post);
    }

    public void deletePost(Long postNo, String username) {
        Post post = postRepository.findById(postNo).orElseThrow(
                () -> new EntityNotFoundException("Post not found with id: " + postNo));
        if (!post.getAuthor().equals(username)) {
            throw new SecurityException("You are not the author of this post.");
        }
        postRepository.deleteById(postNo);
    }

    public void addLike(Long postNo, String username) {
        Post post = postRepository.findById(postNo).orElseThrow(
                () -> new EntityNotFoundException("Post not found with id: " + postNo));
        User user = userService.getUser(username).orElseThrow(
                () -> new EntityNotFoundException("User not found with id: " + username));
        boolean alreadyLiked = likeRepository.existsByUserAndPost(user, post);
        if (alreadyLiked) {
            throw new IllegalStateException("You already liked this post.");
        }
        Like like = new Like(user, post);
        likeRepository.save(like);
    }

    public void removeLike(Long postNo, String username) {
        Post post = postRepository.findById(postNo).orElseThrow(
                () -> new EntityNotFoundException("Post not found with id: " + postNo));
        User user = userService.getUser(username).orElseThrow(
                () -> new EntityNotFoundException("User not found with id: " + username));
        Like like = likeRepository.findByUserAndPost(user, post).orElseThrow(
                () -> new EntityNotFoundException("Like not found with post id: " + postNo + " and user id: " + user.getUsername()));
        likeRepository.delete(like);
    }

    public Post getPost(Long postNo) {
        return postRepository.findById(postNo).orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postNo));
    }

    public void uploadImage(Long postNo, String fileUrl) {
        Post post = postRepository.findById(postNo).orElseThrow(
                () -> new EntityNotFoundException("Post not found with id: " + postNo));
        post.setImageUrl(fileUrl);
        postRepository.save(post);
    }
}