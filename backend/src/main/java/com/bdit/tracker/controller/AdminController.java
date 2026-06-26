package com.bdit.tracker.controller;

import com.bdit.tracker.ingestion.ClinicalTrialsIngestionJob;
import com.bdit.tracker.ingestion.PubMedIngestionJob;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ClinicalTrialsIngestionJob trialsJob;
    private final PubMedIngestionJob pubMedJob;

    public AdminController(ClinicalTrialsIngestionJob trialsJob, PubMedIngestionJob pubMedJob) {
        this.trialsJob = trialsJob;
        this.pubMedJob = pubMedJob;
    }

    @PostMapping("/ingest")
    public Map<String, String> triggerIngestion() {
        new Thread(() -> {
            trialsJob.run();
            pubMedJob.run();
        }).start();
        return Map.of("status", "ingestion started");
    }
}
