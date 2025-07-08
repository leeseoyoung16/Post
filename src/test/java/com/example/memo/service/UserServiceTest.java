package com.example.memo.service;

import com.example.memo.user.User;
import com.example.memo.user.UserRepository;
import com.example.memo.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceTest
{
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("회원가입 성공")
    void 회원가입_성공() {
        //Given
        String username = "testuser";
        String password = "1234567";
        String email = "test@email.com";
        //When
        userService.signup(username, password, email);
        //Then
        User saved = userRepository.findByUsername(username).get();
        assertThat(saved.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("이미 존재하는 사용자 이름으로 가입시 예외 발생")
    void 중복_회원가입_실패() {
        //Given
        String username = "testuser1";
        String password = "1234567";
        String email = "test@email.com";

        userService.signup(username, password, email);
        //When + Then
        assertThatThrownBy(() ->
                userService.signup(username, "anotherpass", "another@email.com")
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 사용자");
    }

    @Test
    @DisplayName("로그인 성공")
    void 로그인_성공() {
        //Given
        String username = "testuser";
        String password = "1234567";
        String email = "test@email.com";
        userService.signup(username, password, email);
        MockHttpServletRequest request = new MockHttpServletRequest();
        //When
        userService.login(username,password,request);
        //Then
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo(username);
    }

    @Test
    @DisplayName("로그인 실패")
    void 로그인_실패() {
        //Given
        String username = "testuser";
        String password = "1234567";
        String email = "test@email.com";
        userService.signup(username, password, email);
        MockHttpServletRequest request = new MockHttpServletRequest();
        //When
        //Then
        assertThatThrownBy(() -> userService.login("uncorrect", password, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 아이디입니다.");
    }
}
