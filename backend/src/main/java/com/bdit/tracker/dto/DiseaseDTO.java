package com.bdit.tracker.dto;

public record DiseaseDTO(Long id, String name, String slug, long trialCount, long publicationCount) {}
