package com.ensar.jobs.service;

import com.ensar.jobs.dto.UserDTO;
import java.util.List;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(String id, UserDTO userDTO);
    UserDTO getUserById(String id);
    List<UserDTO> getAllUsers();
    void deleteUser(String id);
    UserDTO toggleUserStatus(String id);
} 