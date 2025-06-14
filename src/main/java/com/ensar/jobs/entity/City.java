package com.ensar.jobs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "cities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class City {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "rankin")
    private Integer rankin;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 100)
    private String state;
    
    @Column(length = 100)
    private String country;
    
    private Integer population;
    
    private Double growth;
    
    @Column(name = "created_date_time")
    private LocalDateTime createdDateTime;
    
    @Column(name = "last_updated_date_time")
    private LocalDateTime lastUpdatedDateTime;
    
    @PrePersist
    protected void onCreate() {
        if (createdDateTime == null) {
            createdDateTime = LocalDateTime.now();
        }
        if (lastUpdatedDateTime == null) {
            lastUpdatedDateTime = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastUpdatedDateTime = LocalDateTime.now();
    }
} 