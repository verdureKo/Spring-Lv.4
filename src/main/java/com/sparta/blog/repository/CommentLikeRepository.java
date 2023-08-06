package com.sparta.blog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.blog.entity.Comment;
import com.sparta.blog.entity.CommentLike;
import com.sparta.blog.entity.User;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
	Optional<CommentLike> findByUserAndComment(User user, Comment comment);
	Boolean existsByUserAndComment(User user, Comment comment);
}
