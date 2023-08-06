package com.sparta.blog.service;

import com.sparta.blog.dto.CommentRequestDto;
import com.sparta.blog.dto.CommentResponseDto;
import com.sparta.blog.entity.Comment;
import com.sparta.blog.entity.User;

public interface CommentService {

	/**
	 * 댓글 생성
	 * @param requestDto 댓글 생성 요청정보
	 * @param user 댓글 생성 요청자
	 * @return 생성된 댓글 정보
	 */
	CommentResponseDto createComment(CommentRequestDto requestDto, User user);

	/**
	 * 댓글 수정
	 * @param comment
	 * @param requestDto
	 * @param user
	 * @return
	 */
	CommentResponseDto updateComment(Comment comment, CommentRequestDto requestDto, User user);

	void deleteComment(Comment comment, User user);

	void likeComment(Long id, User user);

	void deleteLikeComment(Long id, User user);

	Comment findComment(long id);
}
