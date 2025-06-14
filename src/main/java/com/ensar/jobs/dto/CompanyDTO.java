package com.ensar.jobs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO {
    private String id;
    private String name;
    private String description;
    private String logo;
    private String logoUrl;
    private String website;
    private String industry;
    private String size;
    private Integer employeeCount;
    private Integer founded;
    private String headquarters;
    private BigDecimal rating;
    private String reviews;
    private Integer reviewsCount;
    private Integer jobCount;
    private String color;
    private Boolean isVerified;
    private Boolean isFeatured;
    private String benefits;
    private String culture;
    private String linkedin;
    private String twitter;
    private String facebook;
    private String instagram;
    private String locations;
    private String organizationId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 