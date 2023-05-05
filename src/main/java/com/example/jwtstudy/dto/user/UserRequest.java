package com.example.jwtstudy.dto.user;

import lombok.Getter;
import lombok.Setter;

public class UserRequest {
    @Getter
    @Setter
    public static class LoginInDTO {
        private String username;
        private String password;
    }
}

