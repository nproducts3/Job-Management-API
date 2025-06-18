package com.ensar.jobs.repository;

import com.ensar.jobs.entity.JobResume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobResumeRepository extends JpaRepository<JobResume, String> {
    List<JobResume> findByGooglejobId(String googlejobId);
} 