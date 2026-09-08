package com.seafood.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String role;
    private String type;
    private Long id;
    private String name;
}
