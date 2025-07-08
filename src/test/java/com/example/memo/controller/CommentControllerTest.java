package com.example.memo.controller;

import com.example.memo.comment.Comment;
import com.example.memo.comment.CommentRepository;
import com.example.memo.comment.CommentRequest;
import com.example.memo.post.Post;
import com.example.memo.post.PostRepository;
import com.example.memo.user.CustomUserDetails;
import com.example.memo.user.User;
import com.example.memo.user.UserRepository;
import com.example.memo.user.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class CommentControllerTest
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
    @Autowired
    private CommentRepository commentRepository;

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

        Post saved = postRepository.findAll().get(0);
        Comment comment = new Comment();
        comment.setContent("댓글입니다.");
        comment.setAuthor(user);
        comment.setPost(saved);
        commentRepository.save(comment);
    }

    @Test
    @DisplayName("댓글 등록 성공")
    void 댓글_등록_성공() throws Exception{
        Post saved = postRepository.findAll().get(0);
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setContent("댓글입니다.");

        mockMvc.perform(post("/comments/posts/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("전체 댓글 조회")
    void 전체_댓글_조회() throws Exception {
        mockMvc.perform(get("/comments")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].content").value("댓글입니다."));
    }

    @Test
    @DisplayName("댓글 단건 조회")
    void 댓글_단건_조회() throws Exception {
        Comment savedComment = commentRepository.findAll().get(0);

        mockMvc.perform(get("/comments/" + savedComment.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("댓글입니다."));
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void 댓글_수정_성공() throws Exception {
        Comment savedComment = commentRepository.findAll().get(0);

        CommentRequest updateRequest = new CommentRequest();
        updateRequest.setContent("수정된 댓글입니다");

        mockMvc.perform(put("/comments/" + savedComment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("댓글 수정 실패")
    void 댓글_수정_실패() throws Exception {
        User user2 = new User();
        user2.setUsername("test2");
        user2.setPassword(passwordEncoder.encode("test123456"));
        user2.setEmail("test2@email.com");
        user2.setRole(UserRole.USER);
        userRepository.save(user2);

        CustomUserDetails userDetails = new CustomUserDetails(user2);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        Comment savedComment = commentRepository.findAll().get(0);

        CommentRequest updateRequest = new CommentRequest();
        updateRequest.setContent("다른 사용자의 수정 시도");

        mockMvc.perform(put("/comments/" + savedComment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void 댓글_삭제_성공() throws Exception {
        Comment savedComment = commentRepository.findAll().get(0);

        mockMvc.perform(delete("/comments/" + savedComment.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("댓글 삭제 실패")
    void 댓글_삭제_실패() throws Exception {
        User user2 = new User();
        user2.setUsername("test2");
        user2.setPassword(passwordEncoder.encode("test123456"));
        user2.setEmail("test2@email.com");
        user2.setRole(UserRole.USER);
        userRepository.save(user2);

        CustomUserDetails userDetails = new CustomUserDetails(user2);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        Comment savedComment = commentRepository.findAll().get(0);

        mockMvc.perform(delete("/comments/" + savedComment.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("대댓글 등록 성공")
    void 대댓글_등록_성공() throws Exception {
        Comment parentComment = commentRepository.findAll().get(0);
        Post post = parentComment.getPost();

        CommentRequest replyRequest = new CommentRequest();
        replyRequest.setContent("대댓글입니다.");
        replyRequest.setParentId(parentComment.getId());  // 대댓글의 핵심

        mockMvc.perform(post("/comments/posts/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(replyRequest)))
                .andExpect(status().isCreated());
    }

}
