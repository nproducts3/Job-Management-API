package com.ensar.jobs.service;

import com.ensar.jobs.dto.JobSeekerCertificationDTO;
import com.ensar.jobs.entity.JobSeekerCertification;
import com.ensar.jobs.repository.JobSeekerCertificationRepository;
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
public class JobSeekerCertificationService {

    private final JobSeekerCertificationRepository certificationRepository;
    private final ModelMapper modelMapper;

    public JobSeekerCertificationDTO createJobSeekerCertification(JobSeekerCertificationDTO dto) {
        JobSeekerCertification certification = modelMapper.map(dto, JobSeekerCertification.class);
        certification = certificationRepository.save(certification);
        return modelMapper.map(certification, JobSeekerCertificationDTO.class);
    }

    public JobSeekerCertificationDTO updateJobSeekerCertification(String id, JobSeekerCertificationDTO dto) {
        JobSeekerCertification existingCertification = certificationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Job seeker certification not found with id: " + id));
        
        modelMapper.map(dto, existingCertification);
        existingCertification.setId(id); // Ensure ID is not overwritten
        existingCertification = certificationRepository.save(existingCertification);
        return modelMapper.map(existingCertification, JobSeekerCertificationDTO.class);
    }

    @Transactional(readOnly = true)
    public JobSeekerCertificationDTO getJobSeekerCertificationById(String id) {
        JobSeekerCertification certification = certificationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Job seeker certification not found with id: " + id));
        return modelMapper.map(certification, JobSeekerCertificationDTO.class);
    }

    @Transactional(readOnly = true)
    public List<JobSeekerCertificationDTO> getAllJobSeekerCertifications() {
        return certificationRepository.findAll().stream()
            .map(certification -> modelMapper.map(certification, JobSeekerCertificationDTO.class))
            .collect(Collectors.toList());
    }

    public void deleteJobSeekerCertification(String id) {
        if (!certificationRepository.existsById(id)) {
            throw new EntityNotFoundException("Job seeker certification not found with id: " + id);
        }
        certificationRepository.deleteById(id);
    }
} 