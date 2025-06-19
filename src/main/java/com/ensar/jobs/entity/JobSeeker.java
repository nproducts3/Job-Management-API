package com.ensar.jobs.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;



@Entity
@Table(name = "job_seekers")
@Data
@EqualsAndHashCode(callSuper = true)
public class JobSeeker extends BaseEntity {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "location")
    private String location;

    @Column(name = "phone")
    private String phone;

    @Column(name = "desired_salary")
    private String desiredSalary;

    @Column(name = "preferred_job_types")
    private String preferredJobTypes;
} 