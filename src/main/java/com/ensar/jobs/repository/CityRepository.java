package com.ensar.jobs.repository;

import com.ensar.jobs.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Integer> {
    List<City> findByCountry(String country);
    boolean existsByNameAndCountry(String name, String country);
} 