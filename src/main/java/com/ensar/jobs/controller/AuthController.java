package com.ensar.jobs.controller;

import com.ensar.jobs.dto.JwtResponse;
import com.ensar.jobs.dto.LoginRequest;
import com.ensar.jobs.entity.User;
import com.ensar.jobs.repository.UserRepository;
import com.ensar.jobs.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // First check if user exists and is not disabled
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElse(null);
            
            if (user == null) {
                return ResponseEntity.status(401).body("Invalid email or password");
            }
            
            if (user.getDisabled() != null && user.getDisabled()) {
                return ResponseEntity.status(401).body("Account is disabled. Please contact administrator.");
            }
            
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String role = user.getRole() != null ? user.getRole().getRoleName() : "ROLE_USER";
            String token = jwtTokenProvider.generateToken(authentication, user.getId(), role);
            return ResponseEntity.ok(new JwtResponse(token, user.getId(), user.getEmail(), role));
        } catch (DisabledException e) {
            return ResponseEntity.status(401).body("Account is disabled. Please contact administrator.");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Invalid email or password");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Authentication failed: " + e.getMessage());
        }
    }
} 