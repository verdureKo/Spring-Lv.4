package com.sparta.blog.repository;

import java.util.Optional;

import com.sparta.blog.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.blog.entity.PostLike;
import com.sparta.blog.entity.User;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
	Optional<PostLike> findByUserAndPost(User user, Post post);
	Boolean existsByUserAndPost(User user, Post post);
}
