package com.ensar.jobs.service;

import com.ensar.jobs.dto.OrganizationDTO;
import com.ensar.jobs.entity.Organization;
import com.ensar.jobs.repository.OrganizationRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrganizationService {

    @Autowired
    private OrganizationRepository organizationRepository;

    public List<OrganizationDTO> getAllOrganizations() {
        return organizationRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public OrganizationDTO getOrganizationById(String id) {
        Optional<Organization> organization = organizationRepository.findById(id);
        return organization.map(this::convertToDTO).orElse(null);
    }

    public OrganizationDTO createOrganization(OrganizationDTO organizationDTO) {
        Organization organization = convertToEntity(organizationDTO);
        Organization savedOrganization = organizationRepository.save(organization);
        return convertToDTO(savedOrganization);
    }

    public OrganizationDTO updateOrganization(String id, OrganizationDTO organizationDTO) {
        Optional<Organization> existingOrg = organizationRepository.findById(id);
        if (existingOrg.isPresent()) {
            Organization organization = existingOrg.get();
            BeanUtils.copyProperties(organizationDTO, organization, "id", "createdDateTime", "lastUpdatedDateTime");
            Organization updatedOrganization = organizationRepository.save(organization);
            return convertToDTO(updatedOrganization);
        }
        return null;
    }

    public boolean deleteOrganization(String id) {
        if (organizationRepository.existsById(id)) {
            organizationRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private OrganizationDTO convertToDTO(Organization organization) {
        OrganizationDTO dto = new OrganizationDTO();
        BeanUtils.copyProperties(organization, dto);
        return dto;
    }

    private Organization convertToEntity(OrganizationDTO dto) {
        Organization organization = new Organization();
        BeanUtils.copyProperties(dto, organization);
        return organization;
    }
} 