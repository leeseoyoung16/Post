package com.example.memo.comment;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponse
{
    private Long id;
    private String content;
    private String author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long postId;
    private Long parentId;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.author = comment.getAuthor().getUsername();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.postId = comment.getPost().getId();
        this.postId = (comment.getParent() != null) ? comment.getParent().getId() : null;
    }
}
