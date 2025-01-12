package com.example.hearurbackend.service;

import com.example.hearurbackend.dto.comment.CommentResponseDto;
import com.example.hearurbackend.dto.oauth.CustomOAuth2User;
import com.example.hearurbackend.dto.post.PostRequestDto;
import com.example.hearurbackend.dto.post.PostResponseDto;
import com.example.hearurbackend.entity.community.Comment;
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
import java.util.*;
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
                .map(post -> createPostResponseDto(post, auth))
                .collect(Collectors.toList());
    }

    public PostResponseDto getPostDetail(Long postNo, CustomOAuth2User auth) {
        Post post = postRepository.findById(postNo).orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postNo));
        post.increaseViews();
        postRepository.save(post);
        return createPostResponseDto(post, auth);
    }

    private PostResponseDto createPostResponseDto(Post post, CustomOAuth2User auth) {
        String authorNickname = getAuthorNickname(post.getAuthor());
        boolean isLiked = checkPostLikedByUser(post, auth);
        List<CommentResponseDto> commentDTOList = createCommentDtoList(post.getComments());

        return PostResponseDto.builder()
                .no(post.getNo())
                .category(post.getCategory())
                .title(post.getTitle())
                .author(authorNickname)
                .createDate(post.getCreateDate())
                .updateDate(post.getUpdateDate())
                .isUpdated(post.isUpdated())
                .content(post.getContent())
                .comments(commentDTOList)
                .views(post.getViews())
                .likes(post.getLikesCount())
                .isLiked(isLiked)
                .imageUrls(post.getImageUrl())
                .build();
    }

    private String getAuthorNickname(String authorId) {
        return userService.getUser(authorId)
                .map(User::getNickname)
                .orElse("Unknown Author");
    }

    private boolean checkPostLikedByUser(Post post, CustomOAuth2User auth) {
        if (auth == null) return false;
        return userService.getUser(auth.getUsername())
                .map(user -> likeRepository.existsByUserAndPost(user, post))
                .orElse(false);
    }

    private List<CommentResponseDto> createCommentDtoList(List<Comment> comments) {
        Map<UUID, CommentResponseDto> commentMap = new HashMap<>();
        List<CommentResponseDto> result = new ArrayList<>();

        // 먼저 모든 댓글을 DTO로 변환하고 맵에 저장
        for (Comment comment : comments) {
            CommentResponseDto dto = new CommentResponseDto(
                    comment.getId(),
                    comment.getParentComment() != null ? comment.getParentComment().getId() : null,
                    getAuthorNickname(comment.getAuthor()),
                    comment.getContent(),
                    comment.getCreateDate(),
                    comment.isUpdated(),
                    new ArrayList<>()
            );
            commentMap.put(comment.getId(), dto);
            if (comment.getParentComment() == null) {
                result.add(dto);
            }
        }

        // 자식 댓글을 부모 DTO에 추가
        for (Comment comment : comments) {
            if (comment.getParentComment() != null) {
                CommentResponseDto childDto = commentMap.get(comment.getId());
                CommentResponseDto parentDto = commentMap.get(comment.getParentComment().getId());
                parentDto.getReplies().add(childDto);
            }
        }

        return result;
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
        post.addImageUrl(fileUrl);
        postRepository.save(post);
    }

    public Boolean isFirstPost(String username) {
        return postRepository.existsByAuthor(username);
    }
}