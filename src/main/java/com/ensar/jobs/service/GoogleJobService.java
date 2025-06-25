package com.ensar.jobs.service;

import com.ensar.jobs.dto.GoogleJobDTO;
import com.ensar.jobs.entity.GoogleJob;
import com.ensar.jobs.repository.GoogleJobRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GoogleJobService {

    private final GoogleJobRepository googleJobRepository;

    public GoogleJobDTO createGoogleJob(GoogleJobDTO googleJobDTO) {
        GoogleJob googleJob = mapToEntity(googleJobDTO);
        googleJob = googleJobRepository.save(googleJob);
        return mapToDTO(googleJob);
    }

    public GoogleJobDTO updateGoogleJob(String id, GoogleJobDTO googleJobDTO) {
        GoogleJob existingJob = googleJobRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Google job not found with id: " + id));
        
        updateEntityFromDTO(existingJob, googleJobDTO);
        existingJob.setId(id); // Ensure ID is not overwritten
        existingJob = googleJobRepository.save(existingJob);
        return mapToDTO(existingJob);
    }

    @Transactional(readOnly = true)
    public GoogleJobDTO getGoogleJobById(String id) {
        GoogleJob googleJob = googleJobRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Google job not found with id: " + id));
        return mapToDTO(googleJob);
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
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<GoogleJobDTO> getAllGoogleJobs(Pageable pageable) {
        return googleJobRepository.findAll(pageable)
            .map(this::mapToDTO);
    }

    // Manual mapping methods
    private GoogleJob mapToEntity(GoogleJobDTO dto) {
        GoogleJob entity = new GoogleJob();
        entity.setId(dto.getId());
        entity.setJobId(dto.getJobId());
        entity.setTitle(dto.getTitle());
        entity.setCompanyName(dto.getCompanyName());
        entity.setLocation(dto.getLocation());
        entity.setVia(dto.getVia());
        entity.setShareLink(dto.getShareLink());
        entity.setPostedAt(dto.getPostedAt());
        entity.setSalary(dto.getSalary());
        entity.setScheduleType(dto.getScheduleType());
        entity.setQualifications(dto.getQualifications());
        entity.setDescription(dto.getDescription());
        entity.setResponsibilities(dto.getResponsibilities());
        entity.setBenefits(dto.getBenefits());
        entity.setApplyLinks(dto.getApplyLinks());
        return entity;
    }

    private GoogleJobDTO mapToDTO(GoogleJob entity) {
        GoogleJobDTO dto = new GoogleJobDTO();
        dto.setId(entity.getId());
        dto.setJobId(entity.getJobId());
        dto.setTitle(entity.getTitle());
        dto.setCompanyName(entity.getCompanyName());
        dto.setLocation(entity.getLocation());
        dto.setVia(entity.getVia());
        dto.setShareLink(entity.getShareLink());
        dto.setPostedAt(entity.getPostedAt());
        dto.setSalary(entity.getSalary());
        dto.setScheduleType(entity.getScheduleType());
        dto.setQualifications(entity.getQualifications());
        dto.setDescription(entity.getDescription());
        dto.setResponsibilities(entity.getResponsibilities());
        dto.setBenefits(entity.getBenefits());
        dto.setApplyLinks(entity.getApplyLinks());
        dto.setCreatedDateTime(entity.getCreatedDateTime() != null ? entity.getCreatedDateTime().toString() : null);
        dto.setLastUpdatedDateTime(entity.getLastUpdatedDateTime() != null ? entity.getLastUpdatedDateTime().toString() : null);
        return dto;
    }

    private void updateEntityFromDTO(GoogleJob entity, GoogleJobDTO dto) {
        if (dto.getJobId() != null) entity.setJobId(dto.getJobId());
        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        if (dto.getCompanyName() != null) entity.setCompanyName(dto.getCompanyName());
        if (dto.getLocation() != null) entity.setLocation(dto.getLocation());
        if (dto.getVia() != null) entity.setVia(dto.getVia());
        if (dto.getShareLink() != null) entity.setShareLink(dto.getShareLink());
        if (dto.getPostedAt() != null) entity.setPostedAt(dto.getPostedAt());
        if (dto.getSalary() != null) entity.setSalary(dto.getSalary());
        if (dto.getScheduleType() != null) entity.setScheduleType(dto.getScheduleType());
        if (dto.getQualifications() != null) entity.setQualifications(dto.getQualifications());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getResponsibilities() != null) entity.setResponsibilities(dto.getResponsibilities());
        if (dto.getBenefits() != null) entity.setBenefits(dto.getBenefits());
        if (dto.getApplyLinks() != null) entity.setApplyLinks(dto.getApplyLinks());
    }
} 