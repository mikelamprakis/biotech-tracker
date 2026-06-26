package com.bdit.tracker.dto;

import java.time.LocalDate;

public record PublicationDTO(
        Long id,
        String pubmedId,
        String title,
        String abstrakt,
        String authors,
        String journal,
        LocalDate publishedDate,
        String sourceUrl,
        String diseaseName
) {}
