package com.ensar.jobs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_titles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobTitle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(name = "created_date_time")
    private LocalDateTime createdDateTime;
    
    @Column(name = "last_updated_date_time")
    private LocalDateTime lastUpdatedDateTime;
    
    @PrePersist
    protected void onCreate() {
        createdDateTime = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastUpdatedDateTime = LocalDateTime.now();
    }
} 