package com.example.memo.like;

import com.example.memo.post.Post;
import com.example.memo.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long>
{
    Optional<Like> findByUserAndPost(User user, Post post);
}
