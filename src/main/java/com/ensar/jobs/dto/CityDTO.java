package com.ensar.jobs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CityDTO {
    private Integer id;
    
    private Integer rankin;
    
    @NotBlank(message = "City name is required")
    @Size(max = 100, message = "City name cannot exceed 100 characters")
    private String name;
    
    @Size(max = 100, message = "State name cannot exceed 100 characters")
    private String state;
    
    @Size(max = 100, message = "Country name cannot exceed 100 characters")
    private String country;
    
    private Integer population;
    private Double growth;
    private LocalDateTime createdDateTime;
    private LocalDateTime lastUpdatedDateTime;
} 