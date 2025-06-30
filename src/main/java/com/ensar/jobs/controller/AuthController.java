package com.ensar.jobs.controller;

import com.ensar.jobs.dto.JwtResponse;
import com.ensar.jobs.dto.LoginRequest;
import com.ensar.jobs.entity.User;
import com.ensar.jobs.repository.UserRepository;
import com.ensar.jobs.repository.RoleRepository;
import com.ensar.jobs.entity.Role;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.FirebaseAuthException;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.ensar.jobs.repository.OrganizationRepository;
import com.ensar.jobs.entity.Organization;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OrganizationRepository organizationRepository;

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

    @PostMapping("/firebase-login")
    public ResponseEntity<?> firebaseLogin(@RequestBody Map<String, String> body) {
        String idToken = body.get("idToken");
        if (idToken == null) {
            return ResponseEntity.badRequest().body("Missing idToken");
        }
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String email = decodedToken.getEmail();
            if (email == null) {
                return ResponseEntity.status(401).body("Invalid Firebase token: no email");
            }
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                // Auto-create user if not found
                user = new User();
                user.setEmail(email);
                user.setUsername(email.split("@")[0]);
                user.setEmailVerified(decodedToken.isEmailVerified());
                user.setDisabled(false);
                // Set a random password (hashed) for Google users
                user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                // Set default role (update as needed)
                Role defaultRole = roleRepository.findByRoleName("ROLE_USER").orElse(null);
                user.setRole(defaultRole);
                // Set first name, last name, and profile picture if available
                String fullName = decodedToken.getName();
                if (fullName != null && fullName.contains(" ")) {
                    String[] parts = fullName.split(" ", 2);
                    user.setFirstName(parts[0]);
                    user.setLastName(parts[1]);
                } else if (fullName != null) {
                    user.setFirstName(fullName);
                }
                String picture = (String) decodedToken.getClaims().get("picture");
                if (picture != null) {
                    user.setProfilePicture(picture);
                }
                // Set default organization (first in DB)
                Organization defaultOrg = organizationRepository.findAll().stream().findFirst().orElse(null);
                user.setOrganization(defaultOrg);
                user = userRepository.save(user);
            }
            if (user.getDisabled() != null && user.getDisabled()) {
                return ResponseEntity.status(401).body("Account is disabled. Please contact administrator.");
            }
            String role = user.getRole() != null ? user.getRole().getRoleName() : "ROLE_USER";
            // Optionally, generate a JWT for session unification
            String token = jwtTokenProvider.generateToken(
                new UsernamePasswordAuthenticationToken(user.getEmail(), null),
                user.getId(),
                role
            );
            return ResponseEntity.ok(new JwtResponse(token, user.getId(), user.getEmail(), role));
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid Firebase token: " + e.getMessage());
        }
    }
} 