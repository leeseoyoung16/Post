package com.example.memo.post;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>
{
    List<Post> findByTitleContaining(String keyword);
    List<Post> findAllByOrderByCreatedAtDesc();
}
