package com.example.memo.controller;

import com.example.memo.like.LikeRepository;
import com.example.memo.post.Post;
import com.example.memo.post.PostRepository;
import com.example.memo.user.CustomUserDetails;
import com.example.memo.user.User;
import com.example.memo.user.UserRepository;
import com.example.memo.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class LikeControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long postId;

    @BeforeEach
    void setup()
    {
        User user = new User();
        user.setUsername("test1");
        user.setPassword(passwordEncoder.encode("test123456"));
        user.setEmail("test@email.com");
        user.setRole(UserRole.USER);
        userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Post post = new Post();
        post.setTitle("테스트 제목");
        post.setContent("테스트 내용");
        post.setAuthor(user);
        postRepository.save(post);
        postId = post.getId();
    }

    @Test
    @DisplayName("게시글 좋아요 등록 성공")
    void 좋아요_등록_성공() throws Exception
    {
        mockMvc.perform(post("/posts/" + postId + "/like")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 좋아요 중복 등록 방지")
    void 좋아요_중복_방지() throws Exception
    {

        mockMvc.perform(post("/posts/" + postId + "/like")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post("/posts/" + postId + "/like")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("게시글 좋아요 삭제 성공")
    void 좋아요_삭제_성공() throws Exception
    {
        mockMvc.perform(post("/posts/" + postId + "/like")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/posts/" + postId + "/like")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("게시글 좋아요 삭제 실패")
    void 좋아요_삭제_실패() throws Exception
    {
        mockMvc.perform(delete("/posts/" + postId + "/like")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
