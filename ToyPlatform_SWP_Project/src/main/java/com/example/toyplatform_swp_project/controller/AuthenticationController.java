package com.example.toyplatform_swp_project.controller;

import com.example.toyplatform_swp_project.model.User;
import com.example.toyplatform_swp_project.repository.UserRepository;
import com.example.toyplatform_swp_project.services.implement.AuthenticationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        String response = authenticationService.registerUser(user);
        if (response.contains("OTP sent")) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam int otp) {
        String response = authenticationService.verifyOtp(otp);
        if (response.equals("User registered successfully!")) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user, HttpSession session) {
        String response = authenticationService.loginUser(user.getEmail(), user.getPassword());

        if (response.equals("Login successful!")) {
            Optional<User> authenticatedUser = userRepository.findByEmail(user.getEmail());

            if (authenticatedUser.isPresent()) {
                session.setAttribute("userId", authenticatedUser.get().getUserId());
            }

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest().body(response);
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser() {
        authenticationService.logoutUser();
        return new ResponseEntity<>("Logout successful", HttpStatus.OK);
    }


}
