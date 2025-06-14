package com.ensar.jobs.repository;

import com.ensar.jobs.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Integer> {
    Resume findByEmail(String email);
} 