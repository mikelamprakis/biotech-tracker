package com.bdit.tracker.controller;

import com.bdit.tracker.dto.EventDTO;
import com.bdit.tracker.dto.PagedResponse;
import com.bdit.tracker.service.EventService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public PagedResponse<EventDTO> listEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return eventService.findAll(page, size);
    }
}
