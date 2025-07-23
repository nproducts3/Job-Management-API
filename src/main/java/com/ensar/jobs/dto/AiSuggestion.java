package com.ensar.jobs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiSuggestion {
    private String type;      // e.g., "skill_gap", "improvement", etc.
    private String message;   // The suggestion message
    // Add more fields as needed
}
