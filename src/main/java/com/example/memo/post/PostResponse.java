package com.example.memo.post;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponse
{
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int viewCount;
    private int commentCount;
    private int likesCount;

    public PostResponse(Post post)
    {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.viewCount = post.getViewCount();
        this.commentCount = post.getComments() != null ? post.getComments().size() : 0;
        this.likesCount = post.getLikes() != null ? post.getLikes().size() : 0;
    }
}
