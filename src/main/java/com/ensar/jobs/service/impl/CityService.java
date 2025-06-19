package com.ensar.jobs.service.impl;

import com.ensar.jobs.dto.CityDTO;
import java.util.List;

public interface CityService {
    CityDTO createCity(CityDTO cityDTO);
    CityDTO updateCity(Integer id, CityDTO cityDTO);
    CityDTO getCityById(Integer id);
    List<CityDTO> getAllCities();
    List<CityDTO> getCitiesByCountry(String country);
    void deleteCity(Integer id);
} 