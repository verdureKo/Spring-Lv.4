package com.sparta.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.blog.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
