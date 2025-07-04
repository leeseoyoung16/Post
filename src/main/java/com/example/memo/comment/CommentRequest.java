package com.example.memo.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest
{
    @NotBlank(message = "내용은 필수입니다.")
    private String content;
    private Long parentId;
}
