// package com.ensar.jobs.service;

// import com.ensar.jobs.dto.UserDTO;
// import com.ensar.jobs.entity.User;
// import com.ensar.jobs.repository.UserRepository;
// import jakarta.persistence.EntityNotFoundException;
// import lombok.RequiredArgsConstructor;
// import org.modelmapper.ModelMapper;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import java.util.List;
// import java.util.stream.Collectors;
// import java.util.UUID;

// @Service
// @RequiredArgsConstructor
// @Transactional
// public class UserService {

//     private final UserRepository userRepository;
//     private final ModelMapper modelMapper;
//     private final PasswordEncoder passwordEncoder;

//     public UserDTO createUser(UserDTO userDTO) {
//         if (userRepository.existsByEmail(userDTO.getEmail())) {
//             throw new IllegalArgumentException("User with email " + userDTO.getEmail() + " already exists");
//         }
        
//         User user = modelMapper.map(userDTO, User.class);
//         if (user.getId() == null || user.getId().isEmpty()) {
//             user.setId(UUID.randomUUID().toString());
//         }
//         user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
//         user = userRepository.save(user);
//         return modelMapper.map(user, UserDTO.class);
//     }

//     public UserDTO updateUser(String id, UserDTO userDTO) {
//         User existingUser = userRepository.findById(id)
//             .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        
//         if (!existingUser.getEmail().equals(userDTO.getEmail()) && 
//             userRepository.existsByEmail(userDTO.getEmail())) {
//             throw new IllegalArgumentException("User with email " + userDTO.getEmail() + " already exists");
//         }
        
//         modelMapper.map(userDTO, existingUser);
//         if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
//             existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
//         }
//         existingUser.setId(id); // Ensure ID is not overwritten
//         existingUser = userRepository.save(existingUser);
//         return modelMapper.map(existingUser, UserDTO.class);
//     }

//     @Transactional(readOnly = true)
//     public UserDTO getUserById(String id) {
//         User user = userRepository.findById(id)
//             .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
//         return modelMapper.map(user, UserDTO.class);
//     }

//     @Transactional(readOnly = true)
//     public UserDTO getUserByEmail(String email) {
//         User user = userRepository.findByEmail(email)
//             .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
//         return modelMapper.map(user, UserDTO.class);
//     }

//     @Transactional(readOnly = true)
//     public List<UserDTO> getAllUsers() {
//         return userRepository.findAll().stream()
//             .map(user -> modelMapper.map(user, UserDTO.class))
//             .collect(Collectors.toList());
//     }

//     public void deleteUser(String id) {
//         if (!userRepository.existsById(id)) {
//             throw new EntityNotFoundException("User not found with id: " + id);
//         }
//         userRepository.deleteById(id);
//     }

//     @Transactional(readOnly = true)
//     public boolean existsByEmail(String email) {
//         return userRepository.existsByEmail(email);
//     }

//     public UserDTO toggleUserStatus(String id) {
//         User user = userRepository.findById(id)
//             .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
//         user.setDisabled(!user.getDisabled());
//         user = userRepository.save(user);
//         return modelMapper.map(user, UserDTO.class);
//     }
// } 

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
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("User with email " + userDTO.getEmail() + " already exists");
        }

        User user = modelMapper.map(userDTO, User.class);

        // Set Organization
        if (userDTO.getOrganizationId() != null) {
            Organization org = organizationRepository.findById(userDTO.getOrganizationId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid organizationId: " + userDTO.getOrganizationId()));
            user.setOrganization(org);
        } else {
            user.setOrganization(null);
        }

        // Set Role
        if (userDTO.getRoleId() != null) {
            Role role = roleRepository.findById(userDTO.getRoleId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid roleId: " + userDTO.getRoleId()));
            user.setRole(role);
        } else {
            user.setRole(null);
        }

        if (user.getId() == null || user.getId().isEmpty()) {
            user.setId(UUID.randomUUID().toString());
        }
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user = userRepository.save(user);
        return modelMapper.map(user, UserDTO.class);
    }

    public UserDTO updateUser(String id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        if (!existingUser.getEmail().equals(userDTO.getEmail()) &&
            userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("User with email " + userDTO.getEmail() + " already exists");
        }

        modelMapper.map(userDTO, existingUser);

        // Set Organization
        if (userDTO.getOrganizationId() != null) {
            Organization org = organizationRepository.findById(userDTO.getOrganizationId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid organizationId: " + userDTO.getOrganizationId()));
            existingUser.setOrganization(org);
        } else {
            existingUser.setOrganization(null);
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
        return modelMapper.map(existingUser, UserDTO.class);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(String id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return modelMapper.map(user, UserDTO.class);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        return modelMapper.map(user, UserDTO.class);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
            .map(user -> modelMapper.map(user, UserDTO.class))
            .collect(Collectors.toList());
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
        return modelMapper.map(user, UserDTO.class);
    }
}