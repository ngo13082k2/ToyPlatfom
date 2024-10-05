package com.example.toyplatform_swp_project.services.implement;

import com.example.toyplatform_swp_project.model.User;
import com.example.toyplatform_swp_project.model.enums.Role;
import com.example.toyplatform_swp_project.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HttpSession session;
    public String registerUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser.isPresent()) {
            return "Email already exists!";
        }

        user.setRole(Role.CUSTOMER);
        user.setStatus("ACTIVE");

        userRepository.save(user);
        return "User registered successfully!";
    }


    public String loginUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {

            if (password.equals(user.get().getPassword())) {
                session.setAttribute("currentUser", user.get());
                return "Login successful!";
            } else {
                return "Invalid password!";
            }
        }
        return "User not found!";
    }
    public User getCurrentUser() {
        return (User) session.getAttribute("currentUser");
    }
    public void logoutUser() {
        session.removeAttribute("currentUser");
    }
}
