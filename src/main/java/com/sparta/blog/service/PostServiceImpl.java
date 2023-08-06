package com.sparta.blog.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.sparta.blog.dto.PostListResponseDto;
import com.sparta.blog.dto.PostRequestDto;
import com.sparta.blog.dto.PostResponseDto;
import com.sparta.blog.entity.Post;
import com.sparta.blog.entity.PostLike;
import com.sparta.blog.entity.User;
import com.sparta.blog.repository.PostLikeRepository;
import com.sparta.blog.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jdi.request.DuplicateRequestException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    @Override
    public PostResponseDto createPost(PostRequestDto requestDto, User user) {
        Post post = new Post(requestDto);
        post.setUser(user);

        postRepository.save(post);

        return new PostResponseDto(post);
    }
    @Override
    public PostListResponseDto getPosts() {
        List<PostResponseDto> postList = postRepository.findAll().stream()
                .map(PostResponseDto::new)
                .collect(Collectors.toList());

        return new PostListResponseDto(postList);
    }
    @Override
    public PostResponseDto getPostById(Long id) {
        Post post = findPost(id);

        return new PostResponseDto(post);
    }
    @Override
    public void deletePost(Post post, User user) {
        postRepository.delete(post);
    }

    @Override
    @Transactional
    public void likePost(Long id, User user) {
        Post post = findPost(id);

        // 아래 조건 코드와 동일
        // if (postLikeRepository.findByUserAndPost(user, post).isPresent()) {
        if (postLikeRepository.existsByUserAndPost(user, post)) {
            throw new DuplicateRequestException("이미 좋아요 한 게시글 입니다.");
        } else {
            PostLike postLike = new PostLike(user, post);
            postLikeRepository.save(postLike);
        }
    }

    @Override
    @Transactional
    public void deleteLikePost(Long id, User user) {
        Post post = findPost(id);
        Optional<PostLike> postLikeOptional = postLikeRepository.findByUserAndPost(user, post);
        if (postLikeOptional.isPresent()) {
            postLikeRepository.delete(postLikeOptional.get());
        } else {
            throw new IllegalArgumentException("해당 게시글에 취소할 좋아요가 없습니다.");
        }
    }

    @Override
    @Transactional
    public PostResponseDto updatePost(Post post, PostRequestDto requestDto, User user) {

        post.setTitle(requestDto.getTitle());
        post.setContent(requestDto.getContent());

        return new PostResponseDto(post);
    }

    @Override
    public Post findPost(long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택한 게시글은 존재하지 않습니다.")
        );
    }
}
