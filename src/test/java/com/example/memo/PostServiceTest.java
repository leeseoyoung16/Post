package com.example.memo;

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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PostServiceTest
{
    @Autowired
    private PostService postService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("게시글 등록 성공")
    void 게시글_등록_성공() {
        //Given
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("123456");
        userRepository.save(user);

        String title = "제목";
        String content = "내용";
        //When
        postService.create(title, content, user);
        //Then
        assertThat(postRepository.findByTitleContaining(title).get(0).getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("게시글 조회 시 조회수 증가")
    void 조회수_성공() {
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
        //When
        postService.findById(postId);
        //Then
        assertThat(savePost.getViewCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void 게시글_삭제_성공() {
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
        //When
        postService.delete(postId, user);
        //Then
        assertThat(postRepository.findById(postId)).isEmpty();
    }

    @Test
    @DisplayName("작성자가 아닐 시 삭제 실패")
    void 게시글_삭제_실패() {
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
        //When
        //Then
        assertThatThrownBy(() -> postService.delete(postId,user2))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("작성자 본인만 삭제할 수 있습니다.");
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void 게시글_수정_성공(){
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

        String title2 = "수정된 제목";
        String content2 = "수정된 내용";
        //When
        postService.update(postId, title2, content2, user);
        //Then
        Post updated = postRepository.findById(postId).get();
        assertThat(updated.getTitle()).isEqualTo(title2);
    }

    @Test
    @DisplayName("작성자가 아닐시 수정 실패")
    void 게시글_수정_실패() {
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

        String title2 = "수정된 제목";
        String content2 = "수정된 내용";
        //When
        //Then
        assertThatThrownBy(() -> postService.update(postId, title2, content2, user2))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("작성자 본인만 수정할 수 있습니다.");
    }

}
