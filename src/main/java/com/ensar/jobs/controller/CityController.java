package com.ensar.jobs.controller;

import com.ensar.jobs.dto.CityDTO;
import com.ensar.jobs.service.CityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
@Tag(name = "City Management", description = "APIs for managing cities")
@CrossOrigin(origins = "*")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @PostMapping
    @Operation(summary = "Create a new city", description = "Creates a new city with the provided details")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "City created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CityDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<CityDTO> createCity(
            @Parameter(description = "City data to create", required = true) 
            @Valid @RequestBody CityDTO cityDTO) {
        return new ResponseEntity<>(cityService.createCity(cityDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a city", description = "Updates an existing city's information")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "City updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CityDTO.class))),
        @ApiResponse(responseCode = "404", description = "City not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<CityDTO> updateCity(
            @Parameter(description = "ID of the city to update", required = true) @PathVariable Integer id,
            @Parameter(description = "Updated city data", required = true) 
            @Valid @RequestBody CityDTO cityDTO) {
        CityDTO updatedCity = cityService.updateCity(id, cityDTO);
        return ResponseEntity.ok(updatedCity);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get city by ID", description = "Retrieves a city's information by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "City found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CityDTO.class))),
        @ApiResponse(responseCode = "404", description = "City not found")
    })
    public ResponseEntity<CityDTO> getCityById(
            @Parameter(description = "ID of the city to retrieve", required = true) 
            @PathVariable Integer id) {
        CityDTO city = cityService.getCityById(id);
        return ResponseEntity.ok(city);
    }

    @GetMapping
    @Operation(summary = "Get all cities", description = "Retrieves a list of all cities")
    @ApiResponse(responseCode = "200", description = "List of cities retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = CityDTO.class)))
    public ResponseEntity<List<CityDTO>> getAllCities() {
        List<CityDTO> cities = cityService.getAllCities();
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/country/{country}")
    @Operation(summary = "Get cities by country", description = "Retrieves a list of all cities in a specific country")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of cities retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CityDTO.class))),
        @ApiResponse(responseCode = "404", description = "No cities found for the specified country")
    })
    public ResponseEntity<List<CityDTO>> getCitiesByCountry(
            @Parameter(description = "Country name to filter cities", required = true) 
            @PathVariable String country) {
        List<CityDTO> cities = cityService.getCitiesByCountry(country);
        return ResponseEntity.ok(cities);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a city", description = "Deletes a city by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "City deleted successfully"),
        @ApiResponse(responseCode = "404", description = "City not found")
    })
    public ResponseEntity<Void> deleteCity(
            @Parameter(description = "ID of the city to delete", required = true) 
            @PathVariable Integer id) {
        cityService.deleteCity(id);
        return ResponseEntity.noContent().build();
    }
} 