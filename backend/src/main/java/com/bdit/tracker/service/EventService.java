package com.bdit.tracker.service;

import com.bdit.tracker.dto.EventDTO;
import com.bdit.tracker.dto.PagedResponse;
import com.bdit.tracker.model.Event;
import com.bdit.tracker.repository.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public PagedResponse<EventDTO> findAll(int page, int size) {
        Page<Event> p = eventRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
        return toResponse(p);
    }

    public PagedResponse<EventDTO> findByDiseaseId(Long diseaseId, int page, int size) {
        Page<Event> p = eventRepository.findByDiseaseIdOrderByCreatedAtDesc(
                diseaseId, PageRequest.of(page, size));
        return toResponse(p);
    }

    private PagedResponse<EventDTO> toResponse(Page<Event> p) {
        return new PagedResponse<>(
                p.getContent().stream().map(this::toDTO).toList(),
                p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }

    private EventDTO toDTO(Event e) {
        return new EventDTO(
                e.getId(), e.getEventType(), e.getSummary(), e.getImpactScore(),
                e.getRefType(), e.getRefId(), e.getCreatedAt(),
                e.getDisease().getName(), e.getDisease().getSlug()
        );
    }
}
