package com.example.memo.like;

import com.example.memo.post.Post;
import com.example.memo.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService
{
    final LikeRepository likeRepository;
    final PostRepository postRepository;

    //좋아요 생성
    public void create(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        Like like = new Like(post);
        likeRepository.save(like);

        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);
    }

    //좋아요 취소
    public void delete(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물 없음"));

        List<Like> likes = likeRepository.findByPost(post);
        if(likes.isEmpty()) throw new IllegalArgumentException("좋아요 없음");

        Like lastLike = likes.get(likes.size() - 1);
        likeRepository.delete(lastLike);
        post.setLikeCount(post.getLikeCount()-1);
        postRepository.save(post);

    }
}
