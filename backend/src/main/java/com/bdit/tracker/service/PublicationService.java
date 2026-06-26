package com.bdit.tracker.service;

import com.bdit.tracker.dto.PagedResponse;
import com.bdit.tracker.dto.PublicationDTO;
import com.bdit.tracker.model.Publication;
import com.bdit.tracker.repository.PublicationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class PublicationService {

    private final PublicationRepository publicationRepository;

    public PublicationService(PublicationRepository publicationRepository) {
        this.publicationRepository = publicationRepository;
    }

    public PagedResponse<PublicationDTO> findByDiseaseId(Long diseaseId, int page, int size) {
        Page<Publication> p = publicationRepository.findByDiseaseIdOrderByPublishedDateDesc(
                diseaseId, PageRequest.of(page, size));
        return new PagedResponse<>(
                p.getContent().stream().map(this::toDTO).toList(),
                p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }

    private PublicationDTO toDTO(Publication pub) {
        return new PublicationDTO(
                pub.getId(), pub.getPubmedId(), pub.getTitle(), pub.getAbstrakt(),
                pub.getAuthors(), pub.getJournal(), pub.getPublishedDate(), pub.getSourceUrl(),
                pub.getDisease().getName()
        );
    }
}
