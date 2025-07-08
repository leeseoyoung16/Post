package com.example.memo.controller;

import com.example.memo.post.Post;
import com.example.memo.post.PostRepository;
import com.example.memo.report.Report;
import com.example.memo.report.ReportRepository;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ReportControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ReportRepository reportRepository;

    private Long postId;

    @BeforeEach
    void setup() {
        User user = new User();
        user.setUsername("user1");
        user.setPassword(passwordEncoder.encode("password"));
        user.setEmail("user1@email.com");
        user.setRole(UserRole.USER);
        userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Post post = new Post();
        post.setTitle("신고 테스트용");
        post.setContent("신고 테스트용 내용");
        post.setAuthor(user);
        postRepository.save(post);
        postId = post.getId();
    }

    @Test
    @DisplayName("게시글 신고 등록 성공")
    void 게시글_신고_성공() throws Exception {
        mockMvc.perform(post("/posts/" + postId + "/reports")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 중복 신고 실패")
    void 게시글_중복_신고_실패() throws Exception {
        mockMvc.perform(post("/posts/" + postId + "/reports"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/posts/" + postId + "/reports"))
                .andExpect(status().isBadRequest()); // 예외 핸들러 필요
    }

    @Test
    @DisplayName("관리자 신고 전체 조회 성공")
    void 관리자_신고_전체_조회() throws Exception {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin12345"));
        admin.setEmail("admin@email.com");
        admin.setRole(UserRole.ADMIN);
        userRepository.save(admin);

        CustomUserDetails userDetails = new CustomUserDetails(admin);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(get("/admin/reports"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("일반 유저는 신고 전체 조회 불가")
    @WithMockUser(username = "user1", roles = "USER")
    void 일반유저_신고조회_불가() throws Exception {
        mockMvc.perform(get("/admin/reports"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("5회 이상 신고된 게시글 조회 성공")
    void 다수신고_게시글_조회() throws Exception {
        // 5명의 유저를 만들어서 같은 게시글을 신고함
        for (int i = 1; i <= 5; i++) {
            User user = new User();
            user.setUsername("test" + i);
            user.setPassword(passwordEncoder.encode("pw"));
            user.setEmail("test" + i + "@test.com");
            user.setRole(UserRole.USER);
            userRepository.save(user);
            Post post = postRepository.findById(postId).orElseThrow();

            Report report = new Report(user, post);
            reportRepository.save(report);
        }

        // admin 조회
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin12345"));
        admin.setEmail("admin@email.com");
        admin.setRole(UserRole.ADMIN);
        userRepository.save(admin);

        CustomUserDetails userDetails = new CustomUserDetails(admin);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(get("/admin/reports/danger-posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("신고 테스트용"));
    }
}
