package com.bdit.tracker.repository;

import com.bdit.tracker.model.Trial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrialRepository extends JpaRepository<Trial, Long> {
    Page<Trial> findByDiseaseIdOrderByLastUpdatedDesc(Long diseaseId, Pageable pageable);
    Optional<Trial> findByNctId(String nctId);
    long countByDiseaseId(Long diseaseId);
}
