package com.ensar.jobs.service;

import com.ensar.jobs.dto.CompanyDTO;
import com.ensar.jobs.entity.Company;
import com.ensar.jobs.entity.Organization;
import com.ensar.jobs.repository.CompanyRepository;
import com.ensar.jobs.repository.OrganizationRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    public List<CompanyDTO> getAllCompanies() {
        return companyRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CompanyDTO getCompanyById(String id) {
        Optional<Company> company = companyRepository.findById(id);
        return company.map(this::convertToDTO).orElse(null);
    }

    public List<CompanyDTO> getCompaniesByOrganization(String organizationId) {
        return companyRepository.findByOrganizationId(organizationId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CompanyDTO> getCompaniesByIndustry(String industry) {
        return companyRepository.findByIndustry(industry)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CompanyDTO> getFeaturedCompanies() {
        return companyRepository.findByIsFeatured(true)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CompanyDTO createCompany(CompanyDTO companyDTO) {
        Company company = convertToEntity(companyDTO);
        if (companyDTO.getOrganizationId() != null) {
            Optional<Organization> organization = organizationRepository.findById(companyDTO.getOrganizationId());
            organization.ifPresent(company::setOrganization);
        }
        Company savedCompany = companyRepository.save(company);
        return convertToDTO(savedCompany);
    }

    public CompanyDTO updateCompany(String id, CompanyDTO companyDTO) {
        Optional<Company> existingCompany = companyRepository.findById(id);
        if (existingCompany.isPresent()) {
            Company company = existingCompany.get();
            BeanUtils.copyProperties(companyDTO, company, "id", "createdAt", "updatedAt", "organization");
            
            if (companyDTO.getOrganizationId() != null) {
                Optional<Organization> organization = organizationRepository.findById(companyDTO.getOrganizationId());
                organization.ifPresent(company::setOrganization);
            }
            
            Company updatedCompany = companyRepository.save(company);
            return convertToDTO(updatedCompany);
        }
        return null;
    }

    public boolean deleteCompany(String id) {
        if (companyRepository.existsById(id)) {
            companyRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private CompanyDTO convertToDTO(Company company) {
        CompanyDTO dto = new CompanyDTO();
        BeanUtils.copyProperties(company, dto);
        if (company.getOrganization() != null) {
            dto.setOrganizationId(company.getOrganization().getId());
        }
        return dto;
    }

    private Company convertToEntity(CompanyDTO dto) {
        Company company = new Company();
        BeanUtils.copyProperties(dto, company, "organizationId");
        return company;
    }
} 