package com.sparta.blog.repository;

import com.sparta.blog.entity.Comment;
import com.sparta.blog.entity.Like;
import com.sparta.blog.entity.Post;
import com.sparta.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPost(User user, Post post);

    Optional<Like> findByUserAndComment(User user, Comment comment);
}
