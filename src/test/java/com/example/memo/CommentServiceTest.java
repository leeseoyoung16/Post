package com.example.memo;

import com.example.memo.comment.Comment;
import com.example.memo.comment.CommentRepository;
import com.example.memo.comment.CommentRequest;
import com.example.memo.comment.CommentService;
import com.example.memo.post.Post;
import com.example.memo.post.PostRepository;
import com.example.memo.post.PostService;
import com.example.memo.user.User;
import com.example.memo.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class CommentServiceTest
{
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostService postService;

    @Test
    @DisplayName("댓글 작성 성공")
    void 댓글_작성_성공(){
        //Given
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("123456");
        userRepository.save(user);

        String title = "제목";
        String content = "내용";
        postService.create(title, content, user);
        Post savePost = postRepository.findByTitleContaining(title).get(0);
        Long postId = savePost.getId();

        CommentRequest request = new CommentRequest();
        request.setContent("댓글 작성");
        //When
        commentService.create(postId, request, user);
        //Then
        Post checked = postRepository.findById(postId).get();
        assertThat(checked.getCommentCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void 댓글_수정_성공() {
        //Given
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("123456");
        userRepository.save(user);

        String title = "제목";
        String content = "내용";
        postService.create(title, content, user);
        Post savePost = postRepository.findByTitleContaining(title).get(0);
        Long postId = savePost.getId();

        CommentRequest request = new CommentRequest();
        request.setContent("댓글 작성");
        commentService.create(postId, request, user);

        Comment savedComment = commentRepository.findByPostId(postId).get(0);
        Long commentId = savedComment.getId();
        //When
        commentService.update(commentId, "댓글 수정", user);
        //Then
        Comment updated = commentRepository.findById(commentId).orElseThrow();
        assertThat(updated.getContent()).isEqualTo("댓글 수정");
    }

    @Test
    @DisplayName("대댓글 작성 성공")
    void 대댓글_성공() {
        //Given
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("123456");
        userRepository.save(user);

        String title = "제목";
        String content = "내용";
        postService.create(title, content, user);
        Post savePost = postRepository.findByTitleContaining(title).get(0);
        Long postId = savePost.getId();

        CommentRequest request = new CommentRequest();
        request.setContent("댓글 작성");
        commentService.create(postId, request, user);
        Long savedCommentId = commentRepository.findByPostId(postId).get(0).getId();

        CommentRequest Rerequest = new CommentRequest();
        Rerequest.setContent("대댓글 작성");
        Rerequest.setParentId(savedCommentId);
        //When
        commentService.create(postId, Rerequest, user);
        //Then
        List<Comment> comments = commentRepository.findByPostId(postId);
        Comment childComment = comments.get(1);
        assertThat(childComment.getContent()).isEqualTo("대댓글 작성");
        assertThat(childComment.getParent().getId()).isEqualTo(savedCommentId);
    }

    @Test
    @DisplayName("작성자가 아닐시 댓글 수정 실패")
    void 댓글_수정_실패() {
        //Given
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("123456");
        userRepository.save(user);

        User user2 = new User();
        user2.setUsername("testuser2");
        user2.setPassword("123456");
        userRepository.save(user2);

        String title = "제목";
        String content = "내용";
        postService.create(title, content, user);
        Post savePost = postRepository.findByTitleContaining(title).get(0);
        Long postId = savePost.getId();

        CommentRequest request = new CommentRequest();
        request.setContent("댓글 작성");
        commentService.create(postId, request, user);

        Comment savedComment = commentRepository.findByPostId(postId).get(0);
        Long commentId = savedComment.getId();
        //When
        //Then
        assertThatThrownBy(() -> commentService.update(commentId,"댓글 수정", user2))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("댓글 작성자만 수정할 수 있습니다.");
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void 댓글_삭제_성공() {
        //Given
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("123456");
        userRepository.save(user);

        String title = "제목";
        String content = "내용";
        postService.create(title, content, user);
        Post savePost = postRepository.findByTitleContaining(title).get(0);
        Long postId = savePost.getId();

        CommentRequest request = new CommentRequest();
        request.setContent("댓글 작성");
        commentService.create(postId, request, user);

        Comment savedComment = commentRepository.findByPostId(postId).get(0);
        Long commentId = savedComment.getId();
        //When
        commentService.delete(commentId, user);
        //Then
        Post updated = postRepository.findById(postId).orElseThrow();
        assertThat(updated.getCommentCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("댓글 작성자가 아닐시 삭제 실패")
    void 삭제_실패(){
        //Given
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("123456");
        userRepository.save(user);

        User user2 = new User();
        user2.setUsername("testuser2");
        user2.setPassword("123456");
        userRepository.save(user2);

        String title = "제목";
        String content = "내용";
        postService.create(title, content, user);
        Post savePost = postRepository.findByTitleContaining(title).get(0);
        Long postId = savePost.getId();

        CommentRequest request = new CommentRequest();
        request.setContent("댓글 작성");
        commentService.create(postId, request, user);

        Comment savedComment = commentRepository.findByPostId(postId).get(0);
        Long commentId = savedComment.getId();
        //When
        //Then
        assertThatThrownBy(() -> commentService.delete(commentId, user2))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("댓글 작성자만 삭제할 수 있습니다.");
    }
}
