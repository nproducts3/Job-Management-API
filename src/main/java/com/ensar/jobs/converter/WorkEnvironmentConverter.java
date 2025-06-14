package com.ensar.jobs.converter;

import com.ensar.jobs.entity.Job.WorkEnvironment;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class WorkEnvironmentConverter implements AttributeConverter<WorkEnvironment, String> {

    @Override
    public String convertToDatabaseColumn(WorkEnvironment environment) {
        return environment == null ? null : environment.getValue();
    }

    @Override
    public WorkEnvironment convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }
        try {
            return WorkEnvironment.fromValue(value);
        } catch (IllegalArgumentException e) {
            // If the exact match fails, try to find a case-insensitive match
            for (WorkEnvironment type : WorkEnvironment.values()) {
                if (type.getValue().equalsIgnoreCase(value)) {
                    return type;
                }
            }
            throw e;
        }
    }
} 