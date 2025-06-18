package com.ensar.jobs.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "job_seeker_certifications")
@Data
public class JobSeekerCertification {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_seeker_id", nullable = false)
    private JobSeeker jobSeeker;

    private String certificationName;
} 