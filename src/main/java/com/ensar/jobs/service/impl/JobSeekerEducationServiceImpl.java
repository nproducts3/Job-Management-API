package com.ensar.jobs.service.impl;

import com.ensar.jobs.dto.JobSeekerEducationDTO;
import com.ensar.jobs.entity.JobSeeker;
import com.ensar.jobs.entity.JobSeekerEducation;
import com.ensar.jobs.repository.JobSeekerEducationRepository;
import com.ensar.jobs.repository.JobSeekerRepository;
import com.ensar.jobs.service.JobSeekerEducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobSeekerEducationServiceImpl implements JobSeekerEducationService {

    private final JobSeekerEducationRepository educationRepository;
    private final JobSeekerRepository jobSeekerRepository;

    private JobSeekerEducationDTO convertToDTO(JobSeekerEducation education) {
        if (education == null) {
            return null;
        }
        JobSeekerEducationDTO dto = new JobSeekerEducationDTO();
        dto.setId(education.getId());
        dto.setDegree(education.getDegree());
        dto.setUniversity(education.getUniversity());
        dto.setGraduationYear(education.getGraduationYear());
        if (education.getJobSeeker() != null) {
            dto.setJobSeekerId(education.getJobSeeker().getId());
        }
        return dto;
    }

    private JobSeekerEducation convertToEntity(JobSeekerEducationDTO dto) {
        if (dto == null) {
            return null;
        }
        JobSeekerEducation education = new JobSeekerEducation();
        education.setId(dto.getId());
        education.setDegree(dto.getDegree());
        education.setUniversity(dto.getUniversity());
        education.setGraduationYear(dto.getGraduationYear());
        if (dto.getJobSeekerId() != null) {
            JobSeeker jobSeeker = jobSeekerRepository.findById(dto.getJobSeekerId())
                .orElseThrow(() -> new RuntimeException("Job seeker not found"));
            education.setJobSeeker(jobSeeker);
        }
        return education;
    }

    @Override
    @Transactional
    public List<JobSeekerEducationDTO> getAllJobSeekerEducations() {
        return educationRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JobSeekerEducationDTO getJobSeekerEducationById(String id) {
        JobSeekerEducation education = educationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Job seeker education not found"));
        return convertToDTO(education);
    }

    @Override
    @Transactional
    public JobSeekerEducationDTO createJobSeekerEducation(JobSeekerEducationDTO dto) {
        JobSeekerEducation entity = convertToEntity(dto);
        return convertToDTO(educationRepository.save(entity));
    }

    @Override
    @Transactional
    public JobSeekerEducationDTO updateJobSeekerEducation(String id, JobSeekerEducationDTO dto) {
        JobSeekerEducation existingEducation = educationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Job seeker education not found"));
        existingEducation.setDegree(dto.getDegree());
        existingEducation.setUniversity(dto.getUniversity());
        existingEducation.setGraduationYear(dto.getGraduationYear());
        if (dto.getJobSeekerId() != null) {
            JobSeeker jobSeeker = jobSeekerRepository.findById(dto.getJobSeekerId())
                .orElseThrow(() -> new RuntimeException("Job seeker not found"));
            existingEducation.setJobSeeker(jobSeeker);
        }
        return convertToDTO(educationRepository.save(existingEducation));
    }

    @Override
    @Transactional
    public void deleteJobSeekerEducation(String id) {
        educationRepository.deleteById(id);
    }
} 