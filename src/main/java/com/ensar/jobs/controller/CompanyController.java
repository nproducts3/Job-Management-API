package com.ensar.jobs.controller;

import com.ensar.jobs.dto.CompanyDTO;
import com.ensar.jobs.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@Tag(name = "Company", description = "Company management APIs")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @Operation(summary = "Get all companies", description = "Retrieve a list of all companies")
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved companies",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = CompanyDTO.class))
    )
    @GetMapping
    public ResponseEntity<List<CompanyDTO>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @Operation(summary = "Get company by ID", description = "Retrieve a company by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Company found", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CompanyDTO.class))),
        @ApiResponse(responseCode = "404", description = "Company not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CompanyDTO> getCompanyById(
            @Parameter(description = "ID of the company") @PathVariable String id) {
        CompanyDTO company = companyService.getCompanyById(id);
        if (company != null) {
            return ResponseEntity.ok(company);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Get companies by organization", description = "Retrieve all companies for a specific organization")
    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<CompanyDTO>> getCompaniesByOrganization(
            @Parameter(description = "ID of the organization") @PathVariable String organizationId) {
        return ResponseEntity.ok(companyService.getCompaniesByOrganization(organizationId));
    }

    @Operation(summary = "Get companies by industry", description = "Retrieve all companies in a specific industry")
    @GetMapping("/industry/{industry}")
    public ResponseEntity<List<CompanyDTO>> getCompaniesByIndustry(
            @Parameter(description = "Industry name") @PathVariable String industry) {
        return ResponseEntity.ok(companyService.getCompaniesByIndustry(industry));
    }

    @Operation(summary = "Get featured companies", description = "Retrieve all featured companies")
    @GetMapping("/featured")
    public ResponseEntity<List<CompanyDTO>> getFeaturedCompanies() {
        return ResponseEntity.ok(companyService.getFeaturedCompanies());
    }

    @Operation(summary = "Create a new company", description = "Create a new company with the provided data")
    @ApiResponse(
        responseCode = "200",
        description = "Company created successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = CompanyDTO.class))
    )
    @PostMapping
    public ResponseEntity<CompanyDTO> createCompany(
            @Parameter(description = "Company data") @RequestBody CompanyDTO companyDTO) {
        return ResponseEntity.ok(companyService.createCompany(companyDTO));
    }

    @Operation(summary = "Update a company", description = "Update an existing company by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Company updated successfully"),
        @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CompanyDTO> updateCompany(
            @Parameter(description = "ID of the company") @PathVariable String id,
            @Parameter(description = "Updated company data") @RequestBody CompanyDTO companyDTO) {
        CompanyDTO updatedCompany = companyService.updateCompany(id, companyDTO);
        if (updatedCompany != null) {
            return ResponseEntity.ok(updatedCompany);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Delete a company", description = "Delete a company by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Company deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(
            @Parameter(description = "ID of the company") @PathVariable String id) {
        boolean deleted = companyService.deleteCompany(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 