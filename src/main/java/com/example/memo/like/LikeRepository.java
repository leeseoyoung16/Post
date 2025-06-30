package com.example.memo.like;

import com.example.memo.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long>
{
    List<Like> findByPost(Post post);
}
