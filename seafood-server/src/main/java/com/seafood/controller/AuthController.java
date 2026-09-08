package com.seafood.controller;

import com.seafood.common.BizException;
import com.seafood.common.Result;
import com.seafood.dto.LoginRequest;
import com.seafood.dto.LoginResponse;
import com.seafood.security.JwtUtil;
import com.seafood.security.SecurityUser;
import com.seafood.service.OperationLogService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final OperationLogService operationLogService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                          OperationLogService operationLogService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.operationLogService = operationLogService;
    }

    @PostMapping("/auth/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLoginCode(), request.getPassword()));
        } catch (AuthenticationException e) {
            throw new BizException("登录编码或密码错误");
        }
        SecurityUser user = (SecurityUser) authentication.getPrincipal();
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole(), user.getType());
        operationLogService.log(user.getUsername(), user.getRole(), user.getType(), "登录", user.getUsername(),
                user.getType() == null ? "系统管理端" : user.getType());
        LoginResponse resp = new LoginResponse();
        resp.setToken(token);
        resp.setRole(user.getRole());
        resp.setType(user.getType());
        resp.setId(user.getId());
        resp.setName(user.getUsername());
        return Result.ok(resp);
    }
}
