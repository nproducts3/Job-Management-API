package com.ensar.jobs.service.impl;

import com.ensar.jobs.dto.GoogleJobDTO;
import com.ensar.jobs.entity.GoogleJob;
import com.ensar.jobs.repository.GoogleJobRepository;
import com.ensar.jobs.service.GoogleJobService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GoogleJobServiceImpl implements GoogleJobService {

    private final GoogleJobRepository googleJobRepository;
    private final ModelMapper modelMapper;

    @Override
    public GoogleJobDTO createGoogleJob(GoogleJobDTO googleJobDTO) {
        GoogleJob googleJob = modelMapper.map(googleJobDTO, GoogleJob.class);
        if (googleJob.getId() == null || googleJob.getId().isBlank()) {
            googleJob.setId(UUID.randomUUID().toString());
        }
        googleJob = googleJobRepository.save(googleJob);
        return modelMapper.map(googleJob, GoogleJobDTO.class);
    }

    @Override
    public GoogleJobDTO updateGoogleJob(String id, GoogleJobDTO googleJobDTO) {
        GoogleJob existing = googleJobRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("GoogleJob not found with id: " + id));
        modelMapper.map(googleJobDTO, existing);
        existing.setId(id);
        existing = googleJobRepository.save(existing);
        return modelMapper.map(existing, GoogleJobDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public GoogleJobDTO getGoogleJobById(String id) {
        GoogleJob googleJob = googleJobRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("GoogleJob not found with id: " + id));
        return modelMapper.map(googleJob, GoogleJobDTO.class);
    }

    @Override
    public void deleteGoogleJob(String id) {
        if (!googleJobRepository.existsById(id)) {
            throw new EntityNotFoundException("GoogleJob not found with id: " + id);
        }
        googleJobRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoogleJobDTO> getAllGoogleJobs() {
        return googleJobRepository.findAll().stream()
            .map(job -> modelMapper.map(job, GoogleJobDTO.class))
            .collect(Collectors.toList());
    }
} 