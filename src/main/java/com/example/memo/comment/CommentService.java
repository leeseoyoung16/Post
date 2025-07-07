package com.example.memo.comment;

import com.example.memo.post.Post;
import com.example.memo.post.PostRepository;
import com.example.memo.user.User;
import com.example.memo.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService
{
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    // 댓글 등록
    @Transactional
    public void create(Long postId, CommentRequest request, User user)
    {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setContent(request.getContent());
        comment.setAuthor(user);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        if(request.getParentId() != null) {
            Comment parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다."));
            comment.setParent(parent);
        }
        commentRepository.save(comment);
    }
    //댓글 삭제
    @Transactional
    public void delete(Long id, User user) {
        Comment comment = commentRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("댓글이 없습니다."));
        if(user.getRole() == UserRole.ADMIN) {
            Post post = comment.getPost();
            commentRepository.delete(comment);
            return;
        }
        if(!comment.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("댓글 작성자만 삭제할 수 있습니다.");
        }
        Post post = comment.getPost();
        commentRepository.delete(comment);
    }
    //댓글 모두 조회
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }
    //게시글 당 댓글 조회
    public List<Comment> findByPost(Long postId) {
        return commentRepository.findByPostId(postId);
    }
    //댓글 단건 조회
    public Comment findById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 없습니다."));
    }

    //댓글 페이지 조회
    public Page<Comment> findAllPaged(Long postId, Pageable pageable) {
        return commentRepository.findByPostId(postId, pageable);
    }

    //댓글 수정
    @Transactional
    public void update(Long id, String content, User user)
    {
        Comment comment = commentRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("댓글이 없습니다."));
        if(!comment.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("댓글 작성자만 수정할 수 있습니다.");
        }
        comment.setContent(content);
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

}
