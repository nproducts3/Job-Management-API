package com.ensar.jobs.service;

import com.ensar.jobs.dto.JobSeekerEducationDTO;
import com.ensar.jobs.entity.JobSeekerEducation;
import com.ensar.jobs.repository.JobSeekerEducationRepository;
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
public class JobSeekerEducationService {

    private final JobSeekerEducationRepository educationRepository;
    private final ModelMapper modelMapper;

    public JobSeekerEducationDTO createJobSeekerEducation(JobSeekerEducationDTO dto) {
        JobSeekerEducation education = modelMapper.map(dto, JobSeekerEducation.class);
        education = educationRepository.save(education);
        return modelMapper.map(education, JobSeekerEducationDTO.class);
    }

    public JobSeekerEducationDTO updateJobSeekerEducation(String id, JobSeekerEducationDTO dto) {
        JobSeekerEducation existingEducation = educationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Job seeker education not found with id: " + id));
        
        modelMapper.map(dto, existingEducation);
        existingEducation.setId(id); // Ensure ID is not overwritten
        existingEducation = educationRepository.save(existingEducation);
        return modelMapper.map(existingEducation, JobSeekerEducationDTO.class);
    }

    @Transactional(readOnly = true)
    public JobSeekerEducationDTO getJobSeekerEducationById(String id) {
        JobSeekerEducation education = educationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Job seeker education not found with id: " + id));
        return modelMapper.map(education, JobSeekerEducationDTO.class);
    }

    @Transactional(readOnly = true)
    public List<JobSeekerEducationDTO> getAllJobSeekerEducations() {
        return educationRepository.findAll().stream()
            .map(education -> modelMapper.map(education, JobSeekerEducationDTO.class))
            .collect(Collectors.toList());
    }

    public void deleteJobSeekerEducation(String id) {
        if (!educationRepository.existsById(id)) {
            throw new EntityNotFoundException("Job seeker education not found with id: " + id);
        }
        educationRepository.deleteById(id);
    }
} 