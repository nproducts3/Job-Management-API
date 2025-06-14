package com.ensar.jobs.service;

import com.ensar.jobs.dto.UserDTO;
import java.util.List;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(Integer id, UserDTO userDTO);
    UserDTO getUserById(Integer id);
    List<UserDTO> getAllUsers();
    void deleteUser(Integer id);
    UserDTO toggleUserStatus(Integer id);
} 