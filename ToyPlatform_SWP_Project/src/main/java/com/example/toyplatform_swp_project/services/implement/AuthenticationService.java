package com.example.toyplatform_swp_project.services.implement;

import com.example.toyplatform_swp_project.model.User;
import com.example.toyplatform_swp_project.model.enums.Role;
import com.example.toyplatform_swp_project.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HttpSession session;
    @Autowired
    private JavaMailSender mailSender;
    private int generatedOtp;
    public String registerUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser.isPresent()) {
            User existing = existingUser.get();
            if ("ACTIVE".equals(existing.getStatus())) {
                return "Email already exists!";
            } else if ("PENDING".equals(existing.getStatus())) {
                generatedOtp = new Random().nextInt(900000) + 100000; // Generates a 6-digit OTP

                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(user.getEmail());
                message.setSubject("OTP for Registration");
                message.setText("Your OTP for registration is: " + generatedOtp);

                mailSender.send(message);
                session.setAttribute("registeringUser", existing);
                return "OTP resent to your email. Please verify to complete registration.";
            }
        }

        user.setRole(Role.CUSTOMER);
        user.setStatus("PENDING");

        generatedOtp = new Random().nextInt(900000) + 100000; // Generates a 6-digit OTP
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("OTP for Registration");
        message.setText("Your OTP for registration is: " + generatedOtp);

        mailSender.send(message);

        userRepository.save(user);
        session.setAttribute("registeringUser", user);
        return "OTP sent to your email. Please verify to complete registration.";
    }

    public String verifyOtp(int inputOtp) {
        User user = (User) session.getAttribute("registeringUser");

        if (user == null) {
            return "No user in session. Please start the registration process again.";
        }

        if (inputOtp == generatedOtp) {
            user.setRole(Role.CUSTOMER);
            user.setStatus("ACTIVE");
            userRepository.save(user);

            session.removeAttribute("registeringUser");
            generatedOtp = 0;
            return "User registered successfully!";
        } else {
            return "Invalid OTP. Please try again.";
        }
    }



    public String loginUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            if (!"ACTIVE".equals(user.get().getStatus())) {
                return "User account is not active. Please verify your account.";
            }

            if (password.equals(user.get().getPassword())) {
                session.setAttribute("currentUser", user.get());
                return "Login successful!";
            } else {
                return "Invalid password!";
            }
        }
        return "User not found!";
    }

    public  User getCurrentUser() {
        return (User) session.getAttribute("currentUser");
    }
    public void logoutUser() {
        session.removeAttribute("currentUser");
    }
}
