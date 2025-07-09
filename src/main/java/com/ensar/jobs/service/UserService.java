package com.ensar.jobs.service;

import com.ensar.jobs.dto.UserDTO;
import com.ensar.jobs.entity.Organization;
import com.ensar.jobs.entity.Role;
import com.ensar.jobs.entity.User;
import com.ensar.jobs.repository.OrganizationRepository;
import com.ensar.jobs.repository.RoleRepository;
import com.ensar.jobs.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("User with email " + userDTO.getEmail() + " already exists");
        }

        User user = mapToEntity(userDTO);

        // Always generate a new ID for new users (let JPA handle it)
        user.setId(null);

        // Set Organization
        if (userDTO.getOrganizationId() != null && !userDTO.getOrganizationId().isEmpty()) {
            Organization org = organizationRepository.findById(userDTO.getOrganizationId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid organizationId: " + userDTO.getOrganizationId()));
            user.setOrganization(org);
        } else {
            // Assign the first available organization as default
            Organization defaultOrg = organizationRepository.findAll().stream().findFirst().orElse(null);
            if (defaultOrg == null) {
                throw new IllegalStateException("No organization found in the database to assign to user");
            }
            user.setOrganization(defaultOrg);
        }

        // Set Role
        Role assignedRole = null;
        if (userDTO.getRoleId() != null && !userDTO.getRoleId().isEmpty()) {
            assignedRole = roleRepository.findById(userDTO.getRoleId()).orElse(null);
            if (assignedRole == null) {
                // If provided roleId is invalid, fallback to default role by name
                assignedRole = roleRepository.findByRoleName("ROLE_JOBSEEKER")
                        .orElseThrow(() -> new IllegalArgumentException("Default role ROLE_JOBSEEKER not found in DB"));
            }
        } else {
            // Assign ROLE_JOBSEEKER as default if not provided or empty
            assignedRole = roleRepository.findByRoleName("ROLE_JOBSEEKER")
                    .orElseThrow(() -> new IllegalArgumentException("Default role ROLE_JOBSEEKER not found in DB"));
        }
        user.setRole(assignedRole);

        // Ensure disabled is never null
        if (user.getDisabled() == null) {
            user.setDisabled(false);
        }

        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user = userRepository.save(user);
        return mapToDTO(user);
    }

    public UserDTO updateUser(String id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        if (!existingUser.getEmail().equals(userDTO.getEmail()) &&
            userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("User with email " + userDTO.getEmail() + " already exists");
        }

        updateEntityFromDTO(existingUser, userDTO);

        // Set Organization
        if (userDTO.getOrganizationId() != null && !userDTO.getOrganizationId().isEmpty()) {
            Organization org = organizationRepository.findById(userDTO.getOrganizationId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid organizationId: " + userDTO.getOrganizationId()));
            existingUser.setOrganization(org);
        } else {
            throw new IllegalArgumentException("organizationId is required and cannot be null");
        }

        // Set Role
        if (userDTO.getRoleId() != null) {
            Role role = roleRepository.findById(userDTO.getRoleId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid roleId: " + userDTO.getRoleId()));
            existingUser.setRole(role);
        } else {
            existingUser.setRole(null);
        }

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        existingUser.setId(id); // Ensure ID is not overwritten
        existingUser = userRepository.save(existingUser);
        return mapToDTO(existingUser);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(String id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return mapToDTO(user);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        return mapToDTO(user);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    public Page<UserDTO> getPagedUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::mapToDTO);
    }

    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public UserDTO toggleUserStatus(String id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        user.setDisabled(!user.getDisabled());
        user = userRepository.save(user);
        return mapToDTO(user);
    }

    public List<UserDTO> getAllJobSeekerUsers() {
        return userRepository.findAllByRoleName("ROLE_JOBSEEKER")
                .stream()
                .map(this::mapToDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    public Page<UserDTO> getPagedJobSeekerUsers(Pageable pageable) {
        return userRepository.findAllByRoleName("ROLE_JOBSEEKER", pageable).map(this::mapToDTO);
    }

    // Manual mapping methods
    private User mapToEntity(UserDTO dto) {
        User entity = new User();
        // Do not set ID from DTO for new users
        // entity.setId(dto.getId());
        entity.setUsername(dto.getUsername());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        entity.setEmailVerified(dto.getEmailVerified());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setLastLogin(dto.getLastLogin());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setProfilePicture(dto.getProfilePicture());
        entity.setDisabled(dto.getDisabled());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }

    private UserDTO mapToDTO(User entity) {
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setPassword(null); // Don't expose password
        dto.setCreatedDateTime(entity.getCreatedDateTime());
        dto.setLastUpdatedDateTime(entity.getLastUpdatedDateTime());
        dto.setRoleId(entity.getRole() != null ? entity.getRole().getId() : null);
        dto.setOrganizationId(entity.getOrganization() != null ? entity.getOrganization().getId() : null);
        dto.setEmailVerified(entity.getEmailVerified());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setLastLogin(entity.getLastLogin());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setProfilePicture(entity.getProfilePicture());
        dto.setDisabled(entity.getDisabled());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    private void updateEntityFromDTO(User entity, UserDTO dto) {
        if (dto.getUsername() != null) entity.setUsername(dto.getUsername());
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
        if (dto.getEmailVerified() != null) entity.setEmailVerified(dto.getEmailVerified());
        if (dto.getFirstName() != null) entity.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) entity.setLastName(dto.getLastName());
        if (dto.getLastLogin() != null) entity.setLastLogin(dto.getLastLogin());
        if (dto.getPhoneNumber() != null) entity.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getProfilePicture() != null) entity.setProfilePicture(dto.getProfilePicture());
        if (dto.getDisabled() != null) entity.setDisabled(dto.getDisabled());
        if (dto.getUpdatedAt() != null) entity.setUpdatedAt(dto.getUpdatedAt());
    }
}