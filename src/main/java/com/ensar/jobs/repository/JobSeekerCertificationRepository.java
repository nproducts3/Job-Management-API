package com.ensar.jobs.repository;

import com.ensar.jobs.entity.JobSeekerCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobSeekerCertificationRepository extends JpaRepository<JobSeekerCertification, String> {
} 