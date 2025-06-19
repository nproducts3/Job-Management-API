package com.ensar.jobs.service;

import com.ensar.jobs.dto.CityDTO;
import com.ensar.jobs.entity.City;
import com.ensar.jobs.repository.CityRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;
    private final ModelMapper modelMapper;

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
        City city = modelMapper.map(cityDTO, City.class);
        City savedCity = cityRepository.save(city);
        return modelMapper.map(savedCity, CityDTO.class);
    }

    @Transactional
    public CityDTO updateCity(Integer id, CityDTO cityDTO) {
        City existingCity = cityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("City not found with id: " + id));
        
        if (!existingCity.getName().equals(cityDTO.getName()) || 
            !existingCity.getCountry().equals(cityDTO.getCountry())) {
            validateUniqueCityInCountry(cityDTO);
        }
        
        modelMapper.map(cityDTO, existingCity);
        existingCity.setId(id); // Preserve the ID
        City updatedCity = cityRepository.save(existingCity);
        return modelMapper.map(updatedCity, CityDTO.class);
    }

    public CityDTO getCityById(Integer id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("City not found with id: " + id));
        return modelMapper.map(city, CityDTO.class);
    }

    public List<CityDTO> getAllCities() {
        return cityRepository.findAll().stream()
                .map(city -> modelMapper.map(city, CityDTO.class))
                .collect(Collectors.toList());
    }

    public List<CityDTO> getCitiesByCountry(String country) {
        return cityRepository.findByCountry(country).stream()
                .map(city -> modelMapper.map(city, CityDTO.class))
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
            throw new IllegalArgumentException("City with name " + cityDTO.getName() + 
                    " already exists in country " + cityDTO.getCountry());
        }
    }
} 