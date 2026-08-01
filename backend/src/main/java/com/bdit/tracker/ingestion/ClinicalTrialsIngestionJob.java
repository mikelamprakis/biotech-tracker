package com.bdit.tracker.ingestion;

import com.bdit.tracker.model.Disease;
import com.bdit.tracker.model.Event;
import com.bdit.tracker.model.Trial;
import com.bdit.tracker.repository.DiseaseRepository;
import com.bdit.tracker.repository.EventRepository;
import com.bdit.tracker.repository.TrialRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ClinicalTrialsIngestionJob {

    private static final Logger log = LoggerFactory.getLogger(ClinicalTrialsIngestionJob.class);

    private static final String BASE_URL =
            "https://clinicaltrials.gov/api/v2/studies?query.cond=%s&pageSize=50&sort=LastUpdatePostDate:desc&fields=NCTId,BriefTitle,OverallStatus,Phase,LeadSponsorName,StartDate,LastUpdatePostDate";

    private static final Map<String, String> DISEASE_QUERIES = Map.ofEntries(
            Map.entry("ALS", "Amyotrophic+Lateral+Sclerosis"),
            Map.entry("Alzheimer's Disease", "Alzheimer+Disease"),
            Map.entry("Pancreatic Cancer", "Pancreatic+Cancer"),
            Map.entry("Ankylosing Spondylitis", "Ankylosing+Spondylitis"),
            Map.entry("Brain Cancer", "Brain+Neoplasms"),
            Map.entry("Parkinson's Disease", "Parkinson+Disease"),
            Map.entry("Idiopathic Pulmonary Fibrosis", "Idiopathic+Pulmonary+Fibrosis"),
            // Quoted: unquoted, CTG's term matching pulls in unrelated "chronic … disease"
            // trials (graft-versus-host, leukaemia). The phrase match drops that noise.
            Map.entry("Chronic Obstructive Pulmonary Disease", "%22Chronic+Obstructive+Pulmonary+Disease%22"),
            // MASH was renamed from NASH in 2023; most trials are still registered
            // under the old name, so match both or we'd see ~10% of the field.
            Map.entry("Metabolic Dysfunction-Associated Steatohepatitis",
                    "Metabolic+Dysfunction-Associated+Steatohepatitis+OR+Nonalcoholic+Steatohepatitis"),
            Map.entry("Primary Sclerosing Cholangitis", "Primary+Sclerosing+Cholangitis")
    );

    private final DiseaseRepository diseaseRepository;
    private final TrialRepository trialRepository;
    private final EventRepository eventRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.ingestion.enabled:true}")
    private boolean enabled;

    public ClinicalTrialsIngestionJob(DiseaseRepository diseaseRepository,
                                      TrialRepository trialRepository,
                                      EventRepository eventRepository,
                                      RestTemplate restTemplate,
                                      ObjectMapper objectMapper) {
        this.diseaseRepository = diseaseRepository;
        this.trialRepository = trialRepository;
        this.eventRepository = eventRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(cron = "${app.ingestion.cron:0 0 */8 * * *}")
    public void run() {
        if (!enabled) return;
        log.info("Starting ClinicalTrials ingestion");
        List<Disease> diseases = diseaseRepository.findAll();
        for (Disease disease : diseases) {
            String query = DISEASE_QUERIES.get(disease.getName());
            if (query == null) continue;
            try {
                ingestForDisease(disease, query);
            } catch (Exception e) {
                log.error("Failed to ingest trials for {}: {}", disease.getName(), e.getMessage());
            }
        }
        log.info("ClinicalTrials ingestion complete");
    }

    private void ingestForDisease(Disease disease, String query) throws Exception {
        String url = String.format(BASE_URL, query);
        // URI.create, not the String overload: RestTemplate re-encodes a String URL and would
        // turn the pre-encoded %22 phrase quotes into a literal %2522, silently matching nothing.
        String json = restTemplate.getForObject(URI.create(url), String.class);
        JsonNode root = objectMapper.readTree(json);
        JsonNode studies = root.path("studies");

        for (JsonNode study : studies) {
            JsonNode proto = study.path("protocolSection");
            String nctId = proto.path("identificationModule").path("nctId").asText();
            if (nctId.isBlank()) continue;

            Optional<Trial> existing = trialRepository.findByNctId(nctId);
            Trial trial = existing.orElseGet(Trial::new);
            boolean isNew = trial.getId() == null;

            trial.setDisease(disease);
            trial.setNctId(nctId);
            trial.setTitle(proto.path("identificationModule").path("briefTitle").asText("N/A"));
            trial.setStatus(proto.path("statusModule").path("overallStatus").asText());
            trial.setSponsor(proto.path("sponsorCollaboratorsModule").path("leadSponsor").path("name").asText());

            JsonNode phases = proto.path("designModule").path("phases");
            if (phases.isArray() && phases.size() > 0) {
                trial.setPhase(phases.get(phases.size() - 1).asText());
            }

            String startDateStr = proto.path("statusModule").path("startDateStruct").path("date").asText();
            if (!startDateStr.isBlank()) {
                try {
                    trial.setStartDate(parseFlexibleDate(startDateStr));
                } catch (Exception ignored) {}
            }

            trial.setLastUpdated(LocalDateTime.now());
            trial.setSourceUrl("https://clinicaltrials.gov/study/" + nctId);
            trialRepository.save(trial);

            if (isNew) {
                createEvent(disease, "TRIAL_ADDED",
                        "New trial registered: " + trial.getTitle() + " (Phase: " + trial.getPhase() + ", Status: " + trial.getStatus() + ")",
                        5, "TRIAL", trial.getId());
            }
        }
    }

    private LocalDate parseFlexibleDate(String dateStr) {
        for (DateTimeFormatter fmt : List.of(
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("yyyy-MM"),
                DateTimeFormatter.ofPattern("MMMM d, yyyy"),
                DateTimeFormatter.ofPattern("MMMM yyyy")
        )) {
            try {
                return LocalDate.parse(dateStr, fmt);
            } catch (Exception ignored) {}
        }
        return null;
    }

    private void createEvent(Disease disease, String type, String summary, int impact, String refType, Long refId) {
        Event event = new Event();
        event.setDisease(disease);
        event.setEventType(type);
        event.setSummary(summary);
        event.setImpactScore(impact);
        event.setRefType(refType);
        event.setRefId(refId);
        event.setCreatedAt(LocalDateTime.now());
        eventRepository.save(event);
    }
}
