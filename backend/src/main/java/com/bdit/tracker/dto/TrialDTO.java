package com.bdit.tracker.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TrialDTO(
        Long id,
        String nctId,
        String title,
        String phase,
        String status,
        String sponsor,
        LocalDate startDate,
        LocalDateTime lastUpdated,
        String sourceUrl,
        String diseaseName,
        String companyName
) {}
