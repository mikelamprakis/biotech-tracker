package com.bdit.tracker.controller;

import com.bdit.tracker.dto.*;
import com.bdit.tracker.model.Disease;
import com.bdit.tracker.service.DiseaseService;
import com.bdit.tracker.service.EventService;
import com.bdit.tracker.service.PublicationService;
import com.bdit.tracker.service.TrialService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diseases")
public class DiseaseController {

    private final DiseaseService diseaseService;
    private final TrialService trialService;
    private final PublicationService publicationService;
    private final EventService eventService;

    public DiseaseController(DiseaseService diseaseService, TrialService trialService,
                             PublicationService publicationService, EventService eventService) {
        this.diseaseService = diseaseService;
        this.trialService = trialService;
        this.publicationService = publicationService;
        this.eventService = eventService;
    }

    @GetMapping
    public List<DiseaseDTO> listDiseases() {
        return diseaseService.findAll();
    }

    @GetMapping("/{slug}")
    public DiseaseDTO getDisease(@PathVariable String slug) {
        return diseaseService.findBySlug(slug);
    }

    @GetMapping("/{slug}/trials")
    public PagedResponse<TrialDTO> getTrials(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Disease disease = diseaseService.getEntityBySlug(slug);
        return trialService.findByDiseaseId(disease.getId(), page, size);
    }

    @GetMapping("/{slug}/papers")
    public PagedResponse<PublicationDTO> getPapers(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Disease disease = diseaseService.getEntityBySlug(slug);
        return publicationService.findByDiseaseId(disease.getId(), page, size);
    }

    @GetMapping("/{slug}/events")
    public PagedResponse<EventDTO> getEvents(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Disease disease = diseaseService.getEntityBySlug(slug);
        return eventService.findByDiseaseId(disease.getId(), page, size);
    }
}
