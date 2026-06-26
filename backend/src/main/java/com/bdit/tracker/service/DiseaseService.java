package com.bdit.tracker.service;

import com.bdit.tracker.dto.DiseaseDTO;
import com.bdit.tracker.model.Disease;
import com.bdit.tracker.repository.DiseaseRepository;
import com.bdit.tracker.repository.PublicationRepository;
import com.bdit.tracker.repository.TrialRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DiseaseService {

    private final DiseaseRepository diseaseRepository;
    private final TrialRepository trialRepository;
    private final PublicationRepository publicationRepository;

    public DiseaseService(DiseaseRepository diseaseRepository,
                          TrialRepository trialRepository,
                          PublicationRepository publicationRepository) {
        this.diseaseRepository = diseaseRepository;
        this.trialRepository = trialRepository;
        this.publicationRepository = publicationRepository;
    }

    public List<DiseaseDTO> findAll() {
        return diseaseRepository.findAll().stream().map(this::toDTO).toList();
    }

    public DiseaseDTO findBySlug(String slug) {
        Disease d = diseaseRepository.findBySlug(slug)
                .orElseThrow(() -> new NoSuchElementException("Disease not found: " + slug));
        return toDTO(d);
    }

    public Disease getEntityBySlug(String slug) {
        return diseaseRepository.findBySlug(slug)
                .orElseThrow(() -> new NoSuchElementException("Disease not found: " + slug));
    }

    private DiseaseDTO toDTO(Disease d) {
        long trials = trialRepository.countByDiseaseId(d.getId());
        long papers = publicationRepository.countByDiseaseId(d.getId());
        return new DiseaseDTO(d.getId(), d.getName(), d.getSlug(), trials, papers);
    }
}
