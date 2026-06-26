package com.bdit.tracker.repository;

import com.bdit.tracker.model.Publication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PublicationRepository extends JpaRepository<Publication, Long> {
    Page<Publication> findByDiseaseIdOrderByPublishedDateDesc(Long diseaseId, Pageable pageable);
    Optional<Publication> findByPubmedId(String pubmedId);
    long countByDiseaseId(Long diseaseId);
}
