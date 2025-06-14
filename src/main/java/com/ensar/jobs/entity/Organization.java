package com.ensar.jobs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "organizations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Organization {
    
    @Id
    private String id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 100)
    private String domain;

    @Column(columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean disabled = false;

    @Column(name = "logo_img_src", columnDefinition = "TEXT")
    private String logoImgSrc;

    @CreationTimestamp
    @Column(name = "created_date_time", updatable = false)
    private LocalDateTime createdDateTime;

    @UpdateTimestamp
    @Column(name = "last_updated_date_time")
    private LocalDateTime lastUpdatedDateTime;
} 