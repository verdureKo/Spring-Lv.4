package com.sparta.blog.controller;

import com.sparta.blog.dto.PostRequestDto;
import com.sparta.blog.dto.PostResponseDto;
import com.sparta.blog.security.UserDetailsImpl;
import com.sparta.blog.service.PostService;
import com.sparta.blog.exception.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor

public class PostController {
    private final PostService postService;

    @GetMapping("/post")
    public List<PostResponseDto.PostReadResponseDto> getPosts(){
        return postService.getPosts();
    }

    @PostMapping("/post")
    public PostResponseDto.PostBasicResponseDto createPost(@RequestBody PostRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return postService.createPost(requestDto, userDetails.getUser());
    }

    @GetMapping("/post/{id}")
    public PostResponseDto.PostReadResponseDto getPost(@PathVariable Long id){
        return postService.getPost(id);
    }


    @PutMapping("/post/{id}")
    public PostResponseDto.PostReadResponseDto updatePost(
            @PathVariable Long id, @RequestBody PostRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        return postService.updatePost(id, requestDto, userDetails.getUser());
    }

    @DeleteMapping("/post/{id}")
    public ResponseEntity<Message> deletePost(
            @PathVariable Long id, @RequestBody PostRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        return postService.deletePost(id, requestDto, userDetails.getUser());
    }

    @PostMapping("/post/like/{id}")
    public ResponseEntity<Message> likePost(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.likePost(id, userDetails.getUser());
    }

    @DeleteMapping("/post/like/{id}")
    public ResponseEntity<Message> deleteLikePost(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.deleteLikePost(id, userDetails.getUser());
    }
}
