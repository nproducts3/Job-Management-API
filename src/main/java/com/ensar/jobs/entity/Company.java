package com.ensar.jobs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "companies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    
    @Id
    private String id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(length = 10)
    private String logo;

    @Column(name = "logo_url", columnDefinition = "TEXT")
    private String logoUrl;

    @Column(columnDefinition = "TEXT")
    private String website;

    @Column(length = 100)
    private String industry;

    @Column(length = 50)
    private String size;

    @Column(name = "employee_count")
    private Integer employeeCount;

    private Integer founded;

    @Column(length = 100)
    private String headquarters;

    @Column(precision = 2, scale = 1)
    private BigDecimal rating;

    @Column(length = 100)
    private String reviews;

    @Column(name = "reviews_count")
    private Integer reviewsCount;

    @Column(name = "job_count")
    private Integer jobCount;

    @Column(length = 20)
    private String color;

    @Column(name = "is_verified", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isVerified = false;

    @Column(name = "is_featured", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isFeatured = false;

    @Column(columnDefinition = "JSON")
    private String benefits;

    @Column(columnDefinition = "TEXT")
    private String culture;

    @Column(length = 255)
    private String linkedin;

    @Column(length = 255)
    private String twitter;

    @Column(length = 255)
    private String facebook;

    @Column(length = 255)
    private String instagram;

    @Column(columnDefinition = "JSON")
    private String locations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 