package com.ensar.jobs.converter;

import com.ensar.jobs.entity.Job.WorkType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class WorkTypeConverter implements AttributeConverter<WorkType, String> {

    @Override
    public String convertToDatabaseColumn(WorkType workType) {
        return workType == null ? null : workType.getValue();
    }

    @Override
    public WorkType convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }
        try {
            return WorkType.fromValue(value);
        } catch (IllegalArgumentException e) {
            // If the exact match fails, try to find a case-insensitive match
            for (WorkType type : WorkType.values()) {
                if (type.getValue().equalsIgnoreCase(value)) {
                    return type;
                }
            }
            throw e;
        }
    }
} 