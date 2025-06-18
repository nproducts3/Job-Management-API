package com.ensar.jobs.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "job_seeker_education")
@Data
public class JobSeekerEducation {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_seeker_id", nullable = false)
    private JobSeeker jobSeeker;

    private String degree;
    private String university;
    @Column(name = "graduation_year")
    private Integer graduationYear;
} 