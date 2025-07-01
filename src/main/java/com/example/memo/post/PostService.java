package com.example.memo.post;

import com.example.memo.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService
{
    private final PostRepository postRepository;

    // 게시글 등록
    @Transactional
    public void create(String title, String content, User user) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setViewCount(0);
        post.setAuthor(user);
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
    //페이지별 조회
    public Page<Post> findAllPaged(Pageable pageable) {
        return postRepository.findAll(pageable);
    }
    //특정 단어 포함된 게시글 조회
    public List<Post> findByTitleContaining(String keyword) {
        return postRepository.findByTitleContaining(keyword);
    }
    //게시글 삭제
    @Transactional
    public void delete(Long id, User user) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        if(!post.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("작성자 본인만 삭제할 수 있습니다.");
        }
        postRepository.delete(post);
    }
    // 게시글 수정
    @Transactional
    public void update(Long id, String title, String content, User user) {
        Post post = postRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        if(!post.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("작성자 본인만 수정할 수 있습니다.");
        }
        post.setTitle(title);
        post.setContent(content);
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
    }

}
