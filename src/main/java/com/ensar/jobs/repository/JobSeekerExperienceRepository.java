package com.ensar.jobs.repository;

import com.ensar.jobs.entity.JobSeekerExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobSeekerExperienceRepository extends JpaRepository<JobSeekerExperience, String> {
} 