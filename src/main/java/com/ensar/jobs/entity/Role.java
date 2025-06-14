package com.ensar.jobs.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "role")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "role_name", nullable = false)
    private String roleName;

    @Column(name = "role_description")
    private String roleDescription;

    @Column(name = "role_permission")
    private String rolePermission;

    @Column(name = "created_date_time")
    private LocalDateTime createdDateTime;

    @Column(name = "last_updated_date_time")
    private LocalDateTime lastUpdatedDateTime;

    @PrePersist
    public void prePersist() {
        this.createdDateTime = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.lastUpdatedDateTime = LocalDateTime.now();
    }
} 