package com.example.toyplatform_swp_project.controller;

import com.example.toyplatform_swp_project.dto.UserDTO;
import com.example.toyplatform_swp_project.services.IUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping("")
    public ResponseEntity<UserDTO> getUserById(HttpSession session) {
        UserDTO userDTO = userService.getUserById(session);
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("")
    public ResponseEntity<UserDTO> updateUser(HttpSession session, @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(session, userDTO);
        return ResponseEntity.ok(updatedUser);
    }
}
