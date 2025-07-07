package com.example.memo;

import com.example.memo.like.LikeService;
import com.example.memo.post.Post;
import com.example.memo.post.PostRepository;
import com.example.memo.post.PostService;
import com.example.memo.user.User;
import com.example.memo.user.UserRepository;
import com.example.memo.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class LikeServiceTest
{
    @Autowired
    private LikeService likeService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostService postService;

    @Test
    @DisplayName("좋아요 생성 성공")
    void 좋아요_성공() {
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
        likeService.create(postId, user);
        //Then
        Post updated = postRepository.findById(postId).orElseThrow();
        assertThat(updated.getLikes().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("좋아요 중복시 실패")
    void 좋아요_중복() {
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
        likeService.create(postId, user);
        //When
        //Then
        assertThatThrownBy(() -> likeService.create(postId, user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 좋아요를 눌렀습니다.");
    }

    @Test
    @DisplayName("좋아요 취소")
    void 좋아요_취소(){
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
        likeService.create(postId, user);
        //When
        likeService.delete(postId, user);
        //Then
        Post updated = postRepository.findById(postId).orElseThrow();
        assertThat(updated.getLikes().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("좋아요 누르지 않고 취소시 좋아요 취소 실패")
    void 좋아요_취소_실패(){
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
        //Then
        assertThatThrownBy(() -> likeService.delete(postId, user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("좋아요를 누르지 않았습니다.");
    }

}
