package com.example.memo.controller;

import com.example.memo.post.Post;
import com.example.memo.post.PostRepository;
import com.example.memo.post.PostRequest;
import com.example.memo.user.CustomUserDetails;
import com.example.memo.user.User;
import com.example.memo.user.UserRepository;
import com.example.memo.user.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class PostControllerTest
{
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    }

    @Test
    @DisplayName("게시글_등록")
    void 게시글_등록() throws Exception
    {
        PostRequest postRequest = new PostRequest();
        postRequest.setTitle("title");
        postRequest.setContent("content");

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isCreated());

    }

    @Test
    @DisplayName("전체_조회")
    void 게시글_전체_조회() throws Exception {
        mockMvc.perform(get("/posts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1)) // 저장한 게시글 1개
                .andExpect(jsonPath("$[0].title").value("테스트 제목"))
                .andExpect(jsonPath("$[0].content").value("테스트 내용"));
    }

    @Test
    @DisplayName("게시글 단건 조회")
    void 게시글_단건_조회() throws Exception {
        Post savedPost = postRepository.findAll().get(0);

        mockMvc.perform(get("/posts/" + savedPost.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("테스트 제목"))
                .andExpect(jsonPath("$.content").value("테스트 내용"));
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void 게시글_수정_성공() throws Exception {
        Post savedPost = postRepository.findAll().get(0);

        PostRequest updateRequest = new PostRequest();
        updateRequest.setTitle("수정된 제목");
        updateRequest.setContent("수정된 내용");

        mockMvc.perform(put("/posts/" + savedPost.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 수정 실패")
    void 게시글_수정_실패() throws Exception
    {
        User user2 = new User();
        user2.setUsername("test2");
        user2.setPassword(passwordEncoder.encode("test123456"));
        user2.setEmail("test2@email.com");
        user2.setRole(UserRole.USER);
        userRepository.save(user2);

        CustomUserDetails userDetails = new CustomUserDetails(user2);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        PostRequest updated = new PostRequest();
        updated.setTitle("수정된 제목");
        updated.setContent("수정된 내용");

        Post savedPost = postRepository.findAll().get(0);
        mockMvc.perform(put("/posts/"+savedPost.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isForbidden());

    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void 게시글_삭제_성공() throws Exception {
        Post savedPost = postRepository.findAll().get(0);

        mockMvc.perform(delete("/posts/" + savedPost.getId()))
                .andExpect(status().isNoContent());
    }


    @Test
    @DisplayName("게시글 삭제 실패")
    void 게시글_삭제_실패() throws Exception {
        User user2 = new User();
        user2.setUsername("test2");
        user2.setPassword(passwordEncoder.encode("test123456"));
        user2.setEmail("test2@email.com");
        user2.setRole(UserRole.USER);
        userRepository.save(user2);

        CustomUserDetails userDetails = new CustomUserDetails(user2);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Post savedPost = postRepository.findAll().get(0);
        mockMvc.perform(delete("/posts/"+savedPost.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("게시글 키워드 검색")
    void 게시글_키워드_검색() throws Exception {
        mockMvc.perform(get("/posts/search")
                .param("keyword", "테스트")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("테스트 제목"));
    }

}
