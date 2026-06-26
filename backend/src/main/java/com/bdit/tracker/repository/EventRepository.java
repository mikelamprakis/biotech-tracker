package com.bdit.tracker.repository;

import com.bdit.tracker.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByDiseaseIdOrderByCreatedAtDesc(Long diseaseId, Pageable pageable);
    Page<Event> findAllByOrderByCreatedAtDesc(Pageable pageable);
    List<Event> findByDiseaseIdAndCreatedAtAfterOrderByCreatedAtDesc(Long diseaseId, LocalDateTime after);
}
