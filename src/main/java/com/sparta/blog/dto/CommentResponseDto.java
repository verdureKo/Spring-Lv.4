package com.sparta.blog.dto;

import java.time.LocalDateTime;

import com.sparta.blog.entity.Comment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentResponseDto extends ApiResponseDto {
    private String body;
    private String username;
    private Integer likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public CommentResponseDto(Comment comment) {
        super();
        this.body = comment.getBody();
        this.username = comment.getUser().getUsername();
        this.likeCount = comment.getCommentLikes().size();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
    }
}
