package com.football_club.Auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtAuthRequest {
    private String username;
    private String password;

    public JwtAuthRequest() {
        username = null;
        password = null;
    }

    public JwtAuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
