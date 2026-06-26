package com.bdit.tracker.dto;

import java.time.LocalDateTime;

public record EventDTO(
        Long id,
        String eventType,
        String summary,
        Integer impactScore,
        String refType,
        Long refId,
        LocalDateTime createdAt,
        String diseaseName,
        String diseaseSlug
) {}
