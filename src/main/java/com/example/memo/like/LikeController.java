package com.example.memo.like;

import com.example.memo.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}")
public class LikeController
{
    private final LikeService likeService;

    @PostMapping("/like")
    public ResponseEntity<Void> create(@PathVariable Long postId,
                                       @AuthenticationPrincipal CustomUserDetails userDetails){
        likeService.create(postId, userDetails.getUser());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/like")
    public ResponseEntity<Void> delete(@PathVariable Long postId,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        likeService.delete(postId, userDetails.getUser());
        return ResponseEntity.noContent().build();
    }
}
