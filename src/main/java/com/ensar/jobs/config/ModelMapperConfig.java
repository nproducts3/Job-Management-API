package com.ensar.jobs.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ensar.jobs.entity.City;
import com.ensar.jobs.dto.CityDTO;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        
        // Use strict matching strategy
        modelMapper.getConfiguration()
            .setMatchingStrategy(MatchingStrategies.STRICT)
            .setSkipNullEnabled(true);

       

        // Configure City <-> CityDTO mapping
        modelMapper.createTypeMap(City.class, CityDTO.class)
            .addMappings(mapper -> {
                mapper.map(City::getId, CityDTO::setId);
                mapper.map(City::getName, CityDTO::setName);
                mapper.map(City::getState, CityDTO::setState);
                mapper.map(City::getCountry, CityDTO::setCountry);
                mapper.map(City::getRankin, CityDTO::setRankin);
                mapper.map(City::getPopulation, CityDTO::setPopulation);
                mapper.map(City::getGrowth, CityDTO::setGrowth);
                mapper.map(City::getCreatedDateTime, CityDTO::setCreatedDateTime);
                mapper.map(City::getLastUpdatedDateTime, CityDTO::setLastUpdatedDateTime);
            });

        modelMapper.createTypeMap(CityDTO.class, City.class)
            .addMappings(mapper -> {
                mapper.map(CityDTO::getId, City::setId);
                mapper.map(CityDTO::getName, City::setName);
                mapper.map(CityDTO::getState, City::setState);
                mapper.map(CityDTO::getCountry, City::setCountry);
                mapper.map(CityDTO::getRankin, City::setRankin);
                mapper.map(CityDTO::getPopulation, City::setPopulation);
                mapper.map(CityDTO::getGrowth, City::setGrowth);
                mapper.map(CityDTO::getCreatedDateTime, City::setCreatedDateTime);
                mapper.map(CityDTO::getLastUpdatedDateTime, City::setLastUpdatedDateTime);
            });

        return modelMapper;
    }
} 