package com.ensar.jobs.config;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class JpaConfig {

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
        return hibernateProperties -> 
            hibernateProperties.put("hibernate.physical_naming_strategy", 
                "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
    }
} 