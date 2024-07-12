package com.spring.auth.controller;

import com.spring.auth.model.UserName; // Model class representing a user with username and password
import com.spring.auth.repository.UserRepository; // Interface for user data access
import com.spring.auth.service.AuthenticationRequest; // Class representing an authentication request with username and password
import com.spring.auth.service.CustomUserDetailsService; // Service to load user details by username
import com.spring.auth.utils.JwtUtil; // Utility class for JWT token generation and processing
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * This Spring REST controller handles user registration, login, and a basic "Hello, World!" endpoint.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Registers a new user by saving their details in the database.
     * The password is encoded before saving for security reasons.
     *
     * @param user The user object containing username and password
     * @return A success message if registration is successful
     */
    @PostMapping("/register")
    public String registerUser(@RequestBody UserName user) {
        // Encode the user's password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Save the user to the database
        userRepository.save(user);
        return "User registered successfully";
    }

    /**
     * Logs in a user by attempting to authenticate them.
     * If successful, a JWT token is generated and returned.
     *
     * @param authenticationRequest The authentication request object containing username and password
     * @return A JWT token upon successful login
     * @throws Exception If authentication fails
     */
    @PostMapping("/login")
    public String loginUser(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        return jwt;
    }

    /**
     * A simple endpoint that returns a greeting message.
     *
     * @return The string "Hello, World!"
     */
    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }
}
