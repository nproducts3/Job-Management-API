package com.ensar.jobs.repository;

import com.ensar.jobs.entity.JobSeekerSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobSeekerSkillRepository extends JpaRepository<JobSeekerSkill, String> {
} 