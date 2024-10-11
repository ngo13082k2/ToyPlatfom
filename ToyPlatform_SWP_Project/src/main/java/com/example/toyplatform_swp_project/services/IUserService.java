package com.example.toyplatform_swp_project.services;

import com.example.toyplatform_swp_project.dto.UserDTO;
import jakarta.servlet.http.HttpSession;

public interface IUserService {
    UserDTO updateUser(HttpSession session, UserDTO userDTO);
    UserDTO getUserById(HttpSession session);
}
