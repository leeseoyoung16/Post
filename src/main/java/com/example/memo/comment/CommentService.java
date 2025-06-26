package com.example.memo.comment;

import com.example.memo.post.Post;
import com.example.memo.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService
{
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    // 댓글 등록
    public void create(Long postId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }
    //댓글 삭제
    public void delete(Long id) {
        commentRepository.deleteById(id);
    }
    //댓글 조회
    public List<Comment> findAll(Long postId) {
        return commentRepository.findByPostId(postId);
    }
    //댓글 단건 조회
    public Comment findById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 없습니다."));
    }
    //댓글 수정
    public void update(Long id, String content) {
        Comment comment = findById(id);
        comment.setContent(content);
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

}
