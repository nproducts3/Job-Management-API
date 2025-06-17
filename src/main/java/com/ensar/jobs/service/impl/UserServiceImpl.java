package com.ensar.jobs.service.impl;

import com.ensar.jobs.dto.UserDTO;
import com.ensar.jobs.entity.User;
import com.ensar.jobs.entity.Role;
import com.ensar.jobs.entity.Organization;
import com.ensar.jobs.repository.UserRepository;
import com.ensar.jobs.repository.RoleRepository;
import com.ensar.jobs.repository.OrganizationRepository;
import com.ensar.jobs.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;

    public UserServiceImpl(UserRepository userRepository, 
                         RoleRepository roleRepository,
                         OrganizationRepository organizationRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.organizationRepository = organizationRepository;
    }

    @Override
    @Transactional
        public UserDTO createUser(UserDTO userDTO) {
        if (userDTO.getId() != null) {
            throw new IllegalArgumentException("User ID must be null");
        }
        validateUniqueFields(userDTO, null);
        User user = convertToEntity(userDTO);
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Override
    @Transactional
    public UserDTO updateUser(String id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        
        validateUniqueFields(userDTO, id);
        
        // Update the existing user
        BeanUtils.copyProperties(userDTO, existingUser, "id", "createdAt");
        
        // Set relationships
        // if (userDTO.getRoleId() != null) {
        //     Role role = roleRepository.findById(userDTO.getRoleId())
        //         .orElseThrow(() -> new EntityNotFoundException("Role not found"));
        //     existingUser.setRole(role);
        // }
        
        if (userDTO.getOrganizationId() != null) {
            Organization org = organizationRepository.findById(userDTO.getOrganizationId())
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));
            existingUser.setOrganization(org);
        }

        User updatedUser = userRepository.save(existingUser);
        return convertToDTO(updatedUser);
    }

    @Override
    public UserDTO getUserById(String id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return convertToDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UserDTO toggleUserStatus(String id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        user.setDisabled(!user.getDisabled());
        return convertToDTO(userRepository.save(user));
    }

    private void validateUniqueFields(UserDTO userDTO, String excludeId) {
        // Check username uniqueness
        if (userRepository.findByUsername(userDTO.getUsername())
                .filter(user -> !user.getId().equals(excludeId))
                .isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Check email uniqueness
        if (userRepository.findByEmail(userDTO.getEmail())
                .filter(user -> !user.getId().equals(excludeId))
                .isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
    }

    private User convertToEntity(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);

        // Set role
        if (userDTO.getRoleId() != null) {
            Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
            user.setRole(role);
        }

        // Set organization if provided
        if (userDTO.getOrganizationId() != null) {
            Organization org = organizationRepository.findById(userDTO.getOrganizationId())
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));
            user.setOrganization(org);
        }

        return user;
    }

    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        
        if (user.getRole() != null) {
            userDTO.setRoleId(user.getRole().getId());
        }
        
        if (user.getOrganization() != null) {
            userDTO.setOrganizationId(user.getOrganization().getId());
        }
        
        return userDTO;
    }
} 