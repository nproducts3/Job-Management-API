package com.ensar.jobs.repository;

import com.ensar.jobs.entity.JobResume;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobResumeRepository extends JpaRepository<JobResume, String> {
    List<JobResume> findByGooglejobId(String googlejobId);
    List<JobResume> findByJobSeeker_Id(String jobSeekerId);
    Page<JobResume> findByJobSeeker_Id(String jobSeekerId, Pageable pageable);
    List<JobResume> findByJobSeeker_IdAndGooglejobId(String jobSeekerId, String googleJobId);
    List<JobResume> findByJobSeeker_IdOrderByUploadedAtDesc(String jobSeekerId);
    Optional<JobResume> findFirstByJobSeekerIdOrderByUploadedAtDesc(String jobSeekerId);
} 