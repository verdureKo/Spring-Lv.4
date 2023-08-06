package com.sparta.blog.service;

import java.util.Optional;

import com.sparta.blog.dto.CommentRequestDto;
import com.sparta.blog.dto.CommentResponseDto;
import com.sparta.blog.entity.Post;
import com.sparta.blog.repository.CommentLikeRepository;
import com.sparta.blog.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jdi.request.DuplicateRequestException;
import com.sparta.blog.entity.Comment;
import com.sparta.blog.entity.CommentLike;
import com.sparta.blog.entity.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final PostServiceImpl postService;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    @Override
    public CommentResponseDto createComment(CommentRequestDto requestDto, User user) {
        Post post = postService.findPost(requestDto.getPostId());
        Comment comment = new Comment(requestDto.getBody());
        comment.setUser(user);
        comment.setPost(post);

        var savedComment = commentRepository.save(comment);

        return new CommentResponseDto(savedComment);
    }

    @Override
    public void deleteComment(Comment comment, User user) {
        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public CommentResponseDto updateComment(Comment comment, CommentRequestDto requestDto, User user) {

        comment.setBody(requestDto.getBody());

        return new CommentResponseDto(comment);
    }

    @Override
    @Transactional
    public void likeComment(Long id, User user) {
        Comment comment = findComment(id);

        if (commentLikeRepository.existsByUserAndComment(user, comment)) {
            throw new DuplicateRequestException("이미 좋아요 한 댓글 입니다.");
        } else {
            CommentLike commentLike = new CommentLike(user, comment);
            commentLikeRepository.save(commentLike);
        }
    }
    @Override
    @Transactional
    public void deleteLikeComment(Long id, User user) {
        Comment comment = findComment(id);
        Optional<CommentLike> commentLikeOptional = commentLikeRepository.findByUserAndComment(user, comment);
        if (commentLikeOptional.isPresent()) {
            commentLikeRepository.delete(commentLikeOptional.get());
        } else {
            throw new IllegalArgumentException("해당 댓글에 취소할 좋아요가 없습니다.");
        }
    }

    @Override
    public Comment findComment(long id) {
        return commentRepository.findById(id).orElseThrow(() ->
            new IllegalArgumentException("선택한 댓글은 존재하지 않습니다.")
        );
    }
}
