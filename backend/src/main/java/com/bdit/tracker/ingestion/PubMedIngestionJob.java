package com.bdit.tracker.ingestion;

import com.bdit.tracker.model.Disease;
import com.bdit.tracker.model.Event;
import com.bdit.tracker.model.Publication;
import com.bdit.tracker.repository.DiseaseRepository;
import com.bdit.tracker.repository.EventRepository;
import com.bdit.tracker.repository.PublicationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class PubMedIngestionJob {

    private static final Logger log = LoggerFactory.getLogger(PubMedIngestionJob.class);

    private static final String SEARCH_URL =
            "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&retmax=20&retmode=json&sort=pub+date&term=%s";
    private static final String FETCH_URL =
            "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&retmode=json&id=%s";

    private static final Map<String, String> DISEASE_QUERIES = Map.of(
            "ALS", "amyotrophic+lateral+sclerosis+treatment",
            "Alzheimer's Disease", "alzheimer+disease+therapy",
            "Pancreatic Cancer", "pancreatic+cancer+treatment"
    );

    private final DiseaseRepository diseaseRepository;
    private final PublicationRepository publicationRepository;
    private final EventRepository eventRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.ingestion.enabled:true}")
    private boolean enabled;

    public PubMedIngestionJob(DiseaseRepository diseaseRepository,
                              PublicationRepository publicationRepository,
                              EventRepository eventRepository,
                              RestTemplate restTemplate,
                              ObjectMapper objectMapper) {
        this.diseaseRepository = diseaseRepository;
        this.publicationRepository = publicationRepository;
        this.eventRepository = eventRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(cron = "${app.ingestion.cron:0 30 */8 * * *}")
    public void run() {
        if (!enabled) return;
        log.info("Starting PubMed ingestion");
        List<Disease> diseases = diseaseRepository.findAll();
        for (Disease disease : diseases) {
            String query = DISEASE_QUERIES.get(disease.getName());
            if (query == null) continue;
            try {
                ingestForDisease(disease, query);
            } catch (Exception e) {
                log.error("Failed to ingest publications for {}: {}", disease.getName(), e.getMessage());
            }
        }
        log.info("PubMed ingestion complete");
    }

    private void ingestForDisease(Disease disease, String query) throws Exception {
        String searchUrl = String.format(SEARCH_URL, query);
        String searchJson = restTemplate.getForObject(searchUrl, String.class);
        JsonNode searchRoot = objectMapper.readTree(searchJson);
        JsonNode idList = searchRoot.path("esearchresult").path("idlist");

        List<String> ids = new ArrayList<>();
        for (JsonNode idNode : idList) {
            ids.add(idNode.asText());
        }
        if (ids.isEmpty()) return;

        String fetchUrl = String.format(FETCH_URL, String.join(",", ids));
        String fetchJson = restTemplate.getForObject(fetchUrl, String.class);
        JsonNode fetchRoot = objectMapper.readTree(fetchJson);
        JsonNode results = fetchRoot.path("result");

        for (String id : ids) {
            JsonNode article = results.path(id);
            if (article.isMissingNode()) continue;

            Optional<Publication> existing = publicationRepository.findByPubmedId(id);
            if (existing.isPresent()) continue;

            Publication pub = new Publication();
            pub.setDisease(disease);
            pub.setPubmedId(id);
            pub.setTitle(article.path("title").asText("N/A"));
            pub.setAuthors(extractAuthors(article.path("authors")));
            pub.setJournal(article.path("fulljournalname").asText());
            pub.setSourceUrl("https://pubmed.ncbi.nlm.nih.gov/" + id + "/");

            String pubDateStr = article.path("pubdate").asText();
            try {
                pub.setPublishedDate(parseFlexibleDate(pubDateStr));
            } catch (Exception ignored) {}

            publicationRepository.save(pub);
            createEvent(disease, "PAPER_PUBLISHED",
                    "New publication: " + pub.getTitle(), 4, "PUBLICATION", pub.getId());
        }
    }

    private String extractAuthors(JsonNode authorsNode) {
        List<String> names = new ArrayList<>();
        for (JsonNode a : authorsNode) {
            names.add(a.path("name").asText());
        }
        return String.join(", ", names);
    }

    private LocalDate parseFlexibleDate(String dateStr) {
        for (DateTimeFormatter fmt : List.of(
                DateTimeFormatter.ofPattern("yyyy MMM dd"),
                DateTimeFormatter.ofPattern("yyyy MMM"),
                DateTimeFormatter.ofPattern("yyyy")
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
