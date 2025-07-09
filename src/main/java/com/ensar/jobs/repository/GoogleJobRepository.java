package com.ensar.jobs.repository;

import com.ensar.jobs.entity.GoogleJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GoogleJobRepository extends JpaRepository<GoogleJob, String> {
    Optional<GoogleJob> findByJobId(String jobId);
    // Add custom queries if needed
} 