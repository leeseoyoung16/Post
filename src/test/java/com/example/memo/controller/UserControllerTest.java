package com.example.memo.controller;

import com.example.memo.user.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        User user = new User();
        user.setUsername("test1");
        user.setPassword(passwordEncoder.encode("test123456"));
        user.setEmail("test@email.com");
        user.setRole(UserRole.USER);
        userRepository.save(user);
    }

    @Test
    @DisplayName("로그인 성공")
    void 로그인_성공() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("test1");
        loginRequest.setPassword("test123456");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인 실패")
    void 로그인_실패() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("test1");
        loginRequest.setPassword("test1234567");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원가입 성공")
    void 회원가입_성공() throws Exception
    {
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("test2");
        userRequest.setPassword("test123456");
        userRequest.setEmail("test2@email.com");

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원가입 실패")
    void 회원가입_실패() throws Exception
    {
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("test1");
        userRequest.setPassword("test123456");
        userRequest.setEmail("test2@email.com");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest());
    }

}
