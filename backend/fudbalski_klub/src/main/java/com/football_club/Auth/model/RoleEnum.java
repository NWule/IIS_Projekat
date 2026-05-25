package com.football_club.Auth.model;

import org.springframework.security.core.GrantedAuthority;

public enum RoleEnum implements GrantedAuthority {
    ROLE_HEAD_COACH,
    ROLE_ASSISTANT_COACH,
    ROLE_STATISTICIAN,
    ROLE_SCOUT,
    ROLE_SPORTS_DIRECTOR,
    ROLE_ADMIN,
    ROLE_BUYER;

    @Override
    public String getAuthority() {
        return name();
    }
}
