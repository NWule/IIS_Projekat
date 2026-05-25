package com.football_club.Auth.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserTokenState {
    private String token;
    private Long expiresIn;

    public UserTokenState() {
        token = null;
        expiresIn = null;
    }

    public UserTokenState(String token, long expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }
}
