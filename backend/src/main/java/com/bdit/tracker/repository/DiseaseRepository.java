package com.bdit.tracker.repository;

import com.bdit.tracker.model.Disease;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiseaseRepository extends JpaRepository<Disease, Long> {
    Optional<Disease> findBySlug(String slug);
    Optional<Disease> findByName(String name);
}
