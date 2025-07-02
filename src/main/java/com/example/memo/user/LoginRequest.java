package com.example.memo.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest
{
    @Size(min = 2, max = 20)
    @NotBlank(message = "Id는 필수입니다.")
    private String username;

    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}
