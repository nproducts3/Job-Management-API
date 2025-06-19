package com.ensar.jobs.service;

import com.ensar.jobs.dto.GoogleJobDTO;
import com.ensar.jobs.entity.GoogleJob;
import com.ensar.jobs.repository.GoogleJobRepository;
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
public class GoogleJobService {

    private final GoogleJobRepository googleJobRepository;
    private final ModelMapper modelMapper;

    public GoogleJobDTO createGoogleJob(GoogleJobDTO googleJobDTO) {
        GoogleJob googleJob = modelMapper.map(googleJobDTO, GoogleJob.class);
        googleJob = googleJobRepository.save(googleJob);
        return modelMapper.map(googleJob, GoogleJobDTO.class);
    }

    public GoogleJobDTO updateGoogleJob(String id, GoogleJobDTO googleJobDTO) {
        GoogleJob existingJob = googleJobRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Google job not found with id: " + id));
        
        modelMapper.map(googleJobDTO, existingJob);
        existingJob.setId(id); // Ensure ID is not overwritten
        existingJob = googleJobRepository.save(existingJob);
        return modelMapper.map(existingJob, GoogleJobDTO.class);
    }

    @Transactional(readOnly = true)
    public GoogleJobDTO getGoogleJobById(String id) {
        GoogleJob googleJob = googleJobRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Google job not found with id: " + id));
        return modelMapper.map(googleJob, GoogleJobDTO.class);
    }

    public void deleteGoogleJob(String id) {
        if (!googleJobRepository.existsById(id)) {
            throw new EntityNotFoundException("Google job not found with id: " + id);
        }
        googleJobRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<GoogleJobDTO> getAllGoogleJobs() {
        return googleJobRepository.findAll().stream()
            .map(job -> modelMapper.map(job, GoogleJobDTO.class))
            .collect(Collectors.toList());
    }
} 