package com.ensar.jobs.repository;

import com.ensar.jobs.entity.JobTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JobTitleRepository extends JpaRepository<JobTitle, Long> {
    Optional<JobTitle> findByTitle(String title);
    boolean existsByTitle(String title);
} 