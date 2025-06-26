package com.example.memo.post;

import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService
{
    private final PostRepository postRepository;

    // 게시글 등록
    public void create(String title, String content) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setViewCount(0);
        postRepository.save(post);
    }
    // 단건 조회
    public Post findById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다. ID: " + id));
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
        return post;
    }
    // 전체 조회
    public List<Post> findAll() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }
    //특정 단어 포함된 게시글 조회
    public List<Post> findByTitleContaining(String keyword) {
        return postRepository.findByTitleContaining(keyword);
    }
    //게시글 삭제
    public void delete(Long id) {
        postRepository.deleteById(id);
    }
    // 게시글 수정
    public void update(Long id, String title, String content) {
        Post post = findById(id);
        post.setTitle(title);
        post.setContent(content);
        post.setUpdatedAt(LocalDateTime.now());
    }

}
