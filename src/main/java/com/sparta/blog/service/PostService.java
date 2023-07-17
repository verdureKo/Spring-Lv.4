package com.sparta.blog.service;

import com.sparta.blog.dto.PostRequestDto;
import com.sparta.blog.dto.PostResponseDto;
import com.sparta.blog.entity.Like;
import com.sparta.blog.entity.Post;
import com.sparta.blog.entity.User;
import com.sparta.blog.entity.UserRoleEnum;
import com.sparta.blog.repository.LikeRepository;
import com.sparta.blog.repository.PostRepository;
import com.sparta.blog.exception.Message;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor

public class PostService {
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final MessageSource messageSource;


    public List<PostResponseDto.PostReadResponseDto> getPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream().map(PostResponseDto.PostReadResponseDto::new).toList();
    }

    public PostResponseDto.PostBasicResponseDto createPost(PostRequestDto requestDto, User user) {
        Post post = postRepository.save(new Post(requestDto, user));
        return new PostResponseDto.PostBasicResponseDto(post);
    }

    public PostResponseDto.PostReadResponseDto getPost(Long id) {
        Post post = findPost(id);
        return ResponseEntity.ok().body(new PostResponseDto.PostReadResponseDto(post)).getBody();
    }

    @Transactional
    public PostResponseDto.PostReadResponseDto updatePost(Long id, PostRequestDto requestDto, User user) {
        Post post = findPost(id);
        confirmUser(post, user);
        post.update(requestDto);
        return ResponseEntity.ok().body(new PostResponseDto.PostReadResponseDto(post)).getBody();
    }

    public ResponseEntity<Message> deletePost(Long id, PostRequestDto requestDto, User user){
        Post post = findPost(id);
        confirmUser(post, user);
        
        postRepository.delete(post);
        String msg ="삭제 완료";
        Message message = new Message(msg, HttpStatus.OK.value());
        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }

    private Post findPost(Long id){
        return postRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException(messageSource.getMessage(
                        "not.exist.post",
                        null,
                        "해당 게시물이 존재하지 않습니다",
                        Locale.getDefault()
                ))
        );
    }

    private void confirmUser(Post post, User user) {
        UserRoleEnum userRoleEnum = user.getRole();
        if (userRoleEnum == UserRoleEnum.USER && !Objects.equals(post.getUser().getId(), user.getId())) {
            throw new IllegalArgumentException(messageSource.getMessage(
                    "not.your.post",
                    null,
                    "작성자만 수정 및 삭제가 가능합니다",
                    Locale.getDefault()
            ));
        }
    }

    @Transactional
    public ResponseEntity<Message> likePost(Long id, User user) {
        Post post = findPost(id);
        if(likeRepository.findByUserAndPost(user, post).isPresent()){
            throw new IllegalArgumentException(messageSource.getMessage(
                    "already.like",
                    null,
                    "이미 좋아요 되어 있습니다",
                    Locale.getDefault()
            ));
        }
        Like like = likeRepository.save(new Like(user, post));
        String msg ="좋아요 완료";
        Message message = new Message(msg, HttpStatus.OK.value());
        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Message> deleteLikePost(Long id, User user){
        Post post = findPost(id);
        if(likeRepository.findByUserAndPost(user, post).isEmpty()){
            throw new IllegalArgumentException(messageSource.getMessage(
                    "already.delete.like",
                    null,
                    "이미 좋아요 되어 있지 않습니다",
                    Locale.getDefault()
            ));
        }
        Optional<Like> like = likeRepository.findByUserAndPost(user, post);
        likeRepository.delete(like.get());
        post.decreaseLikeCount();
        String msg ="좋아요 취소";
        Message message = new Message(msg, HttpStatus.OK.value());
        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }
}