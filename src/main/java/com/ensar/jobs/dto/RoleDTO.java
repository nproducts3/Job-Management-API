package com.ensar.jobs.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RoleDTO {
    private String id;
    private String roleName;
    private String roleDescription;
    private String rolePermission;
    private LocalDateTime createdDateTime;
    private LocalDateTime lastUpdatedDateTime;
} 