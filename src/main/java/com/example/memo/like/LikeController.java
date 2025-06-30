package com.example.memo.like;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}")
public class LikeController
{
    private final LikeService likeService;

    @PostMapping("/like")
    public ResponseEntity<Void> create(@PathVariable Long postId){
        likeService.create(postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/like")
    public ResponseEntity<Void> delete(@PathVariable Long postId) {
        likeService.delete(postId);
        return ResponseEntity.noContent().build();
    }
}
