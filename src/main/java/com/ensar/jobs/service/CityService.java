package com.ensar.jobs.service;

import com.ensar.jobs.dto.CityDTO;
import com.ensar.jobs.entity.City;
import com.ensar.jobs.repository.CityRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    @PostConstruct
    @Transactional
    public void fixZeroDates() {
        List<City> cities = cityRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        boolean needsUpdate = false;
        
        for (City city : cities) {
            if (city.getCreatedDateTime() == null) {
                city.setCreatedDateTime(now);
                needsUpdate = true;
            }
            if (city.getLastUpdatedDateTime() == null) {
                city.setLastUpdatedDateTime(now);
                needsUpdate = true;
            }
        }
        
        if (needsUpdate) {
            cityRepository.saveAll(cities);
        }
    }

    @Transactional
    public CityDTO createCity(CityDTO cityDTO) {
        validateUniqueCityInCountry(cityDTO);
        City city = mapToEntity(cityDTO);
        City savedCity = cityRepository.save(city);
        return mapToDTO(savedCity);
    }

    @Transactional
    public CityDTO updateCity(Integer id, CityDTO cityDTO) {
        City existingCity = cityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("City not found with id: " + id));
        
        if (!existingCity.getName().equals(cityDTO.getName()) || 
            !existingCity.getCountry().equals(cityDTO.getCountry())) {
            validateUniqueCityInCountry(cityDTO);
        }
        
        updateEntityFromDTO(existingCity, cityDTO);
        existingCity.setId(id); // Preserve the ID
        City updatedCity = cityRepository.save(existingCity);
        return mapToDTO(updatedCity);
    }

    public CityDTO getCityById(Integer id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("City not found with id: " + id));
        return mapToDTO(city);
    }

    public List<CityDTO> getAllCities() {
        return cityRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<CityDTO> getCitiesByCountry(String country) {
        return cityRepository.findByCountry(country).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteCity(Integer id) {
        if (!cityRepository.existsById(id)) {
            throw new EntityNotFoundException("City not found with id: " + id);
        }
        cityRepository.deleteById(id);
    }

    private void validateUniqueCityInCountry(CityDTO cityDTO) {
        if (cityRepository.existsByNameAndCountry(cityDTO.getName(), cityDTO.getCountry())) {
            throw new IllegalArgumentException("City with name '" + cityDTO.getName() + 
                "' already exists in country '" + cityDTO.getCountry() + "'");
        }
    }

    // Manual mapping methods
    private City mapToEntity(CityDTO dto) {
        City entity = new City();
        entity.setId(dto.getId());
        entity.setRankn(dto.getRankn());
        entity.setName(dto.getName());
        entity.setState(dto.getState());
        entity.setCountry(dto.getCountry());
        entity.setPopulation(dto.getPopulation());
        entity.setGrowth(dto.getGrowth());
        entity.setCreatedDateTime(dto.getCreatedDateTime());
        entity.setLastUpdatedDateTime(dto.getLastUpdatedDateTime());
        return entity;
    }

    private CityDTO mapToDTO(City entity) {
        CityDTO dto = new CityDTO();
        dto.setId(entity.getId());
        dto.setRankn(entity.getRankn());
        dto.setName(entity.getName());
        dto.setState(entity.getState());
        dto.setCountry(entity.getCountry());
        dto.setPopulation(entity.getPopulation());
        dto.setGrowth(entity.getGrowth());
        dto.setCreatedDateTime(entity.getCreatedDateTime());
        dto.setLastUpdatedDateTime(entity.getLastUpdatedDateTime());
        return dto;
    }

    private void updateEntityFromDTO(City entity, CityDTO dto) {
        if (dto.getRankn() != null) entity.setRankn(dto.getRankn());
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getState() != null) entity.setState(dto.getState());
        if (dto.getCountry() != null) entity.setCountry(dto.getCountry());
        if (dto.getPopulation() != null) entity.setPopulation(dto.getPopulation());
        if (dto.getGrowth() != null) entity.setGrowth(dto.getGrowth());
    }
} 