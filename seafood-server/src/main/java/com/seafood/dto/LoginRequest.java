package com.seafood.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "登录编码不能为空")
    private String loginCode;
    @NotBlank(message = "登录密码不能为空")
    private String password;
}
