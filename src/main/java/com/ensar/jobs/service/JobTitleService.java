package com.ensar.jobs.service;

import com.ensar.jobs.dto.JobTitleDTO;
import com.ensar.jobs.entity.JobTitle;
import com.ensar.jobs.repository.JobTitleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class JobTitleService {

    private final JobTitleRepository jobTitleRepository;

    public JobTitleService(JobTitleRepository jobTitleRepository) {
        this.jobTitleRepository = jobTitleRepository;
    }

    public JobTitleDTO createJobTitle(JobTitleDTO jobTitleDTO) {
        if (jobTitleRepository.existsByTitle(jobTitleDTO.getTitle())) {
            throw new IllegalArgumentException("Job title already exists");
        }
        JobTitle jobTitle = new JobTitle();
        BeanUtils.copyProperties(jobTitleDTO, jobTitle);
        JobTitle savedJobTitle = jobTitleRepository.save(jobTitle);
        BeanUtils.copyProperties(savedJobTitle, jobTitleDTO);
        return jobTitleDTO;
    }

    public JobTitleDTO updateJobTitle(Long id, JobTitleDTO jobTitleDTO) {
        JobTitle jobTitle = jobTitleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Job title not found with id: " + id));
        
        if (!jobTitle.getTitle().equals(jobTitleDTO.getTitle()) && 
            jobTitleRepository.existsByTitle(jobTitleDTO.getTitle())) {
            throw new IllegalArgumentException("Job title already exists");
        }
        
        BeanUtils.copyProperties(jobTitleDTO, jobTitle, "id", "createdDateTime");
        JobTitle updatedJobTitle = jobTitleRepository.save(jobTitle);
        BeanUtils.copyProperties(updatedJobTitle, jobTitleDTO);
        return jobTitleDTO;
    }

    public JobTitleDTO getJobTitleById(Long id) {
        JobTitle jobTitle = jobTitleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Job title not found with id: " + id));
        JobTitleDTO jobTitleDTO = new JobTitleDTO();
        BeanUtils.copyProperties(jobTitle, jobTitleDTO);
        return jobTitleDTO;
    }

    public List<JobTitleDTO> getAllJobTitles() {
        return jobTitleRepository.findAll().stream()
                .map(jobTitle -> {
                    JobTitleDTO dto = new JobTitleDTO();
                    BeanUtils.copyProperties(jobTitle, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void deleteJobTitle(Long id) {
        if (!jobTitleRepository.existsById(id)) {
            throw new EntityNotFoundException("Job title not found with id: " + id);
        }
        jobTitleRepository.deleteById(id);
    }
} 