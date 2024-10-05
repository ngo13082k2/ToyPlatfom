package com.example.toyplatform_swp_project.services;

import com.example.toyplatform_swp_project.dto.UserDTO;

public interface IUserService {
    UserDTO updateUser(Long userId, UserDTO userDTO);
    UserDTO getUserById(Long userId);
}
