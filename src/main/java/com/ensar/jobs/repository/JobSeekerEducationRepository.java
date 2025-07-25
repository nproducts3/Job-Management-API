package com.ensar.jobs.repository;

import com.ensar.jobs.entity.JobSeekerEducation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobSeekerEducationRepository extends JpaRepository<JobSeekerEducation, String> {
    java.util.List<JobSeekerEducation> findByJobSeekerId(String jobSeekerId);
} 