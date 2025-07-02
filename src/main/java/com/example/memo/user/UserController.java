package com.example.memo.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/auth")
public class UserController
{
    private final UserService userService;

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody UserRequest userRequest) {
        userService.signup(userRequest.getUsername(), userRequest.getPassword(), userRequest.getEmail());
        return ResponseEntity.ok().build();
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest httpRequest) {
        userService.login(loginRequest.getUsername(), loginRequest.getPassword(), httpRequest);
        return ResponseEntity.ok().build();
    }
}
