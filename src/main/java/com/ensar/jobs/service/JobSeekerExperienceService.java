package com.ensar.jobs.service;

import com.ensar.jobs.dto.JobSeekerExperienceDTO;
import com.ensar.jobs.entity.JobSeekerExperience;
import com.ensar.jobs.repository.JobSeekerExperienceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class JobSeekerExperienceService {

    private final JobSeekerExperienceRepository experienceRepository;
    private final ModelMapper modelMapper;

    public JobSeekerExperienceDTO createJobSeekerExperience(JobSeekerExperienceDTO dto) {
        JobSeekerExperience experience = modelMapper.map(dto, JobSeekerExperience.class);
        experience = experienceRepository.save(experience);
        return modelMapper.map(experience, JobSeekerExperienceDTO.class);
    }

    public JobSeekerExperienceDTO updateJobSeekerExperience(String id, JobSeekerExperienceDTO dto) {
        JobSeekerExperience existingExperience = experienceRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Job seeker experience not found with id: " + id));
        
        modelMapper.map(dto, existingExperience);
        existingExperience.setId(id); // Ensure ID is not overwritten
        existingExperience = experienceRepository.save(existingExperience);
        return modelMapper.map(existingExperience, JobSeekerExperienceDTO.class);
    }

    @Transactional(readOnly = true)
    public JobSeekerExperienceDTO getJobSeekerExperienceById(String id) {
        JobSeekerExperience experience = experienceRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Job seeker experience not found with id: " + id));
        return modelMapper.map(experience, JobSeekerExperienceDTO.class);
    }

    @Transactional(readOnly = true)
    public List<JobSeekerExperienceDTO> getAllJobSeekerExperiences() {
        return experienceRepository.findAll().stream()
            .map(experience -> modelMapper.map(experience, JobSeekerExperienceDTO.class))
            .collect(Collectors.toList());
    }

    public void deleteJobSeekerExperience(String id) {
        if (!experienceRepository.existsById(id)) {
            throw new EntityNotFoundException("Job seeker experience not found with id: " + id);
        }
        experienceRepository.deleteById(id);
    }
} 