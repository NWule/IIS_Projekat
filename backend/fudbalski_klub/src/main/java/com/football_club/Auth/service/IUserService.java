package com.football_club.Auth.service;

import com.football_club.Auth.dto.UserDTO;
import com.football_club.Auth.model.RoleEnum;

import java.util.List;

public interface IUserService {

    UserDTO registerUser(UserDTO userDTO);

    UserDTO getUserById(Long id);

    UserDTO getUserByUsername(String username);

    List<UserDTO> getAllUsers();

    List<UserDTO> getUsersByRole(RoleEnum role);

    UserDTO updateUser(Long id, UserDTO userDTO);

    void changeUserRole(Long id, RoleEnum newRole);

    void changeUserStatus(Long id, boolean isActive);
}