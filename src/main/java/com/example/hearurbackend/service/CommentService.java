package com.example.hearurbackend.service;

import com.example.hearurbackend.dto.comment.CommentResponseDto;
import com.example.hearurbackend.entity.community.Comment;
import com.example.hearurbackend.entity.community.Post;
import com.example.hearurbackend.repository.CommentRepository;
import com.example.hearurbackend.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional
    public Comment createComment(Long postNo, String username, CommentResponseDto commentDTO) {
        Post post = postRepository.findById(postNo).orElseThrow(
                () -> new EntityNotFoundException("Post not found"));

        Comment newComment = Comment.builder()
                .content(commentDTO.getContent())
                .author(username)
                .createDate(LocalDateTime.now())
                .post(post)
                .build();

        return commentRepository.save(newComment);
    }

    @Transactional
    public void updateComment(String username, UUID commentId, CommentResponseDto commentDTO) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Comment not found"));

        if (!comment.getAuthor().equals(username)) {
            throw new SecurityException("You are not the author of this comment");
        }

        comment.updateComment(commentDTO.getContent());
        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(UUID commentId, String username) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Post not found with id: " + commentId));
        if (!comment.getAuthor().equals(username)) {
            throw new SecurityException("You are not the author of this post.");
        }
        commentRepository.delete(comment);
    }
}
