package com.ensar.jobs.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table ( name = "job_seeker_education")
@Data
@EqualsAndHashCode(callSuper = true)
public class JobSeekerEducation extends BaseEntity {
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