package com.ensar.jobs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDTO {
    private String id;
    private String name;
    private String description;
    private String domain;
    private Boolean disabled;
    private String logoImgSrc;
    private LocalDateTime createdDateTime;
    private LocalDateTime lastUpdatedDateTime;
} 