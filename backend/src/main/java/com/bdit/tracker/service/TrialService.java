package com.bdit.tracker.service;

import com.bdit.tracker.dto.PagedResponse;
import com.bdit.tracker.dto.TrialDTO;
import com.bdit.tracker.model.Trial;
import com.bdit.tracker.repository.TrialRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class TrialService {

    private final TrialRepository trialRepository;

    public TrialService(TrialRepository trialRepository) {
        this.trialRepository = trialRepository;
    }

    public PagedResponse<TrialDTO> findByDiseaseId(Long diseaseId, int page, int size) {
        Page<Trial> p = trialRepository.findByDiseaseIdOrderByLastUpdatedDesc(
                diseaseId, PageRequest.of(page, size));
        return new PagedResponse<>(
                p.getContent().stream().map(this::toDTO).toList(),
                p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }

    private TrialDTO toDTO(Trial t) {
        return new TrialDTO(
                t.getId(), t.getNctId(), t.getTitle(), t.getPhase(), t.getStatus(),
                t.getSponsor(), t.getStartDate(), t.getLastUpdated(), t.getSourceUrl(),
                t.getDisease().getName(),
                t.getCompany() != null ? t.getCompany().getName() : null
        );
    }
}
