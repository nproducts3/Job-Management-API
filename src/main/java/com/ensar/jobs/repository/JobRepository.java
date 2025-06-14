package com.ensar.jobs.repository;

import com.ensar.jobs.entity.Job;
import com.ensar.jobs.entity.Job.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, String> {
    List<Job> findByStatus(JobStatus status);
    List<Job> findByCompanyEntity_Id(String companyId);
    List<Job> findByCity_Id(Integer cityId);
    List<Job> findByIsFeaturedTrue();
    List<Job> findByIsUrgentTrue();
    List<Job> findByRecruiter_Id(String recruiterId);
    
    @Query("SELECT DISTINCT j FROM Job j WHERE " +
           "LOWER(j.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(j.company) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(j.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(j.department) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(j.location) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Job> searchJobs(@Param("searchTerm") String searchTerm);
    
    List<Job> findBySalaryMinGreaterThanEqualAndSalaryMaxLessThanEqual(
        Integer minSalary, Integer maxSalary);
    
    List<Job> findByExperienceMinGreaterThanEqualAndExperienceMaxLessThanEqual(
        Integer minExperience, Integer maxExperience);
    
    List<Job> findByExpiryDateBetweenAndStatus(
        LocalDateTime start, LocalDateTime end, JobStatus status);
    
    List<Job> findByPostedDateGreaterThanEqual(LocalDateTime date);
    
    Long countByStatus(JobStatus status);
    Long countByCompanyEntity_Id(String companyId);

    List<Job> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrRequirementsContainingIgnoreCase(
            String title, String description, String requirements);

    @Query("SELECT j FROM Job j WHERE " +
           "(:title IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:location IS NULL OR LOWER(j.city.name) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:salary IS NULL OR (j.salaryMin >= CAST(:salary AS int) AND j.salaryMax <= CAST(:salary AS int))) AND " +
           "(:company IS NULL OR LOWER(j.company) LIKE LOWER(CONCAT('%', :company, '%')))")
    List<Job> findByFilters(@Param("title") String title,
                           @Param("location") String location,
                           @Param("salary") String salary,
                           @Param("company") String company);

    @Query("SELECT j FROM Job j WHERE " +
           "(:skill IS NULL OR LOWER(j.requirements) LIKE LOWER(CONCAT('%', :skill, '%'))) AND " +
           "(:minExperience IS NULL OR j.experienceMin >= :minExperience) AND " +
           "(:maxExperience IS NULL OR j.experienceMax <= :maxExperience) AND " +
           "(:cityId IS NULL OR j.city.id = :cityId) AND " +
           "(:searchTerm IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(j.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Job> searchJobsWithFilters(
            @Param("skill") String skill,
            @Param("minExperience") Integer minExperience,
            @Param("maxExperience") Integer maxExperience,
            @Param("cityId") Integer cityId,
            @Param("searchTerm") String searchTerm);

    @Query("SELECT DISTINCT j FROM Job j WHERE " +
           "(:skills IS NULL OR " +
           "LOWER(j.title) LIKE LOWER(CONCAT('%', :skills, '%')) OR " +
           "LOWER(j.requirements) LIKE LOWER(CONCAT('%', :skills, '%')) OR " +
           "LOWER(j.description) LIKE LOWER(CONCAT('%', :skills, '%'))) AND " +
           "(:minExperience IS NULL OR j.experienceMin >= :minExperience) AND " +
           "(:maxExperience IS NULL OR j.experienceMax <= :maxExperience) AND " +
           "(:cityId IS NULL OR j.city.id = :cityId)")
    List<Job> findBySkillsExperienceAndLocation(
            @Param("skills") String skills,
            @Param("minExperience") Integer minExperience,
            @Param("maxExperience") Integer maxExperience,
            @Param("cityId") Integer cityId);
} 