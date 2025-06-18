package com.ensar.jobs.service.impl;

import com.ensar.jobs.dto.JobSeekerExperienceDTO;
import com.ensar.jobs.entity.JobSeeker;
import com.ensar.jobs.entity.JobSeekerExperience;
import com.ensar.jobs.repository.JobSeekerExperienceRepository;
import com.ensar.jobs.repository.JobSeekerRepository;
import com.ensar.jobs.service.JobSeekerExperienceService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobSeekerExperienceServiceImpl implements JobSeekerExperienceService {
    @Autowired
    private JobSeekerExperienceRepository experienceRepository;
    @Autowired
    private JobSeekerRepository jobSeekerRepository;
    @Autowired
    private ModelMapper modelMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public JobSeekerExperienceDTO createJobSeekerExperience(JobSeekerExperienceDTO dto) {
        JobSeekerExperience entity = modelMapper.map(dto, JobSeekerExperience.class);
        if (dto.getJobSeekerId() != null) {
            JobSeeker jobSeeker = jobSeekerRepository.findById(dto.getJobSeekerId()).orElseThrow();
            entity.setJobSeeker(jobSeeker);
        }
        if (dto.getStartDate() != null) {
            entity.setStartDate(LocalDate.parse(dto.getStartDate(), DATE_FORMATTER));
        }
        if (dto.getEndDate() != null) {
            entity.setEndDate(LocalDate.parse(dto.getEndDate(), DATE_FORMATTER));
        }
        return modelMapper.map(experienceRepository.save(entity), JobSeekerExperienceDTO.class);
    }

    @Override
    public JobSeekerExperienceDTO updateJobSeekerExperience(String id, JobSeekerExperienceDTO dto) {
        JobSeekerExperience entity = experienceRepository.findById(id).orElseThrow();
        modelMapper.map(dto, entity);
        if (dto.getJobSeekerId() != null) {
            JobSeeker jobSeeker = jobSeekerRepository.findById(dto.getJobSeekerId()).orElseThrow();
            entity.setJobSeeker(jobSeeker);
        }
        if (dto.getStartDate() != null) {
            entity.setStartDate(LocalDate.parse(dto.getStartDate(), DATE_FORMATTER));
        }
        if (dto.getEndDate() != null) {
            entity.setEndDate(LocalDate.parse(dto.getEndDate(), DATE_FORMATTER));
        }
        return modelMapper.map(experienceRepository.save(entity), JobSeekerExperienceDTO.class);
    }

    @Override
    public JobSeekerExperienceDTO getJobSeekerExperienceById(String id) {
        return experienceRepository.findById(id)
            .map(entity -> modelMapper.map(entity, JobSeekerExperienceDTO.class))
            .orElse(null);
    }

    @Override
    public List<JobSeekerExperienceDTO> getAllJobSeekerExperiences() {
        return experienceRepository.findAll().stream()
            .map(entity -> modelMapper.map(entity, JobSeekerExperienceDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public void deleteJobSeekerExperience(String id) {
        experienceRepository.deleteById(id);
    }
} 