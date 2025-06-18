package com.ensar.jobs.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "job_seeker_experience")
@Data
public class JobSeekerExperience {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_seeker_id", nullable = false)
    private JobSeeker jobSeeker;

    private String jobTitle;
    private String companyName;
    private LocalDate startDate;
    private LocalDate endDate;

    @Column(columnDefinition = "json")
    private String responsibilities;
} 