package com.ensar.jobs.service;

import com.ensar.jobs.dto.RoleDTO;
import com.ensar.jobs.entity.Role;
import com.ensar.jobs.repository.RoleRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public RoleDTO getRoleById(String id) {
        return roleRepository.findById(id).map(this::toDTO).orElse(null);
    }

    public RoleDTO getRoleByName(String roleName) {
        Optional<Role> role = roleRepository.findByRoleName(roleName);
        return role.map(this::toDTO).orElse(null);
    }

    public RoleDTO createRole(RoleDTO dto) {
        Role role = new Role();
        BeanUtils.copyProperties(dto, role);
        Role saved = roleRepository.save(role);
        return toDTO(saved);
    }

    public RoleDTO updateRole(String id, RoleDTO dto) {
        Optional<Role> optRole = roleRepository.findById(id);
        if (optRole.isPresent()) {
            Role role = optRole.get();
            BeanUtils.copyProperties(dto, role, "id", "createdDateTime");
            Role saved = roleRepository.save(role);
            return toDTO(saved);
        }
        return null;
    }

    public void deleteRole(String id) {
            roleRepository.deleteById(id);
    }

    private RoleDTO toDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        BeanUtils.copyProperties(role, dto);
        return dto;
    }
} 