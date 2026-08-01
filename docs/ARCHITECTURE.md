# Biotech Disease Intelligence Tracker — Architecture

> A public web app that tracks real-time biotech progress (clinical trials, research papers,
> regulatory/development events) for a curated set of diseases and translates raw medical data
> into simple, structured signal.

**Currently tracked diseases (10):** ALS, Alzheimer's Disease, Pancreatic Cancer, Ankylosing Spondylitis,
Brain Cancer, Parkinson's Disease, Idiopathic Pulmonary Fibrosis, Chronic Obstructive Pulmonary Disease,
Metabolic Dysfunction-Associated Steatohepatitis, Primary Sclerosing Cholangitis.

This document has two halves:

1. **High-Level Design (HLD)** — the system shape, the moving parts, and how data flows.
2. **Low-Level Design (LLD)** — every layer down to the class/file that performs each step, plus a
   process-to-code map so you can jump straight to the code responsible for any behaviour.

---

## 1. High-Level Design (HLD)

### 1.1 System context

```
                    ┌──────────────────────────────┐
   External data    │   clinicaltrials.gov  (v2)   │
   sources          │   eutils.ncbi.nlm.nih.gov    │  (PubMed E-utilities)
                    └──────────────┬───────────────┘
                                   │  scheduled HTTPS pulls (every 8h)
                                   ▼
   ┌────────────────┐      ┌───────────────────────────────┐      ┌──────────────┐
   │  Browser (SPA) │─────▶│   Spring Boot Monolith (API)  │─────▶│  PostgreSQL  │
   │  React + Vite  │◀─────│   REST + Scheduler + Ingest   │◀─────│              │
   └────────────────┘ JSON └───────────────────────────────┘ JPA  └──────────────┘
     Cloudflare Pages          Render (Docker) / Oracle VM
```

- **One deployable backend artifact** (`biotech-tracker.jar`) — no microservices, no queue, no cache.
- The frontend is a **static SPA** that talks to the backend purely over a read-only JSON REST API.
- Ingestion is **pull-based and scheduled** inside the same process — there is no separate worker.

### 1.2 Component overview

```
Spring Boot Monolith
├── Web / API layer          @RestController  — read-only GET endpoints (+ 1 admin POST)
├── Service layer            @Service         — business logic, entity→DTO mapping, pagination
├── Ingestion layer          @Scheduled jobs  — fetch, normalize, upsert, emit Events
├── Persistence layer        Spring Data JPA repositories over JPA entities
├── Config                   RestTemplate, ObjectMapper, CORS
└── Schema/versioning        Flyway migrations (V1, V2, V3…)
```

### 1.3 Two primary flows

**A. Read flow (user opens the app):**

```
Browser → GET /api/... → Controller → Service → Repository → PostgreSQL
                                          │
                                          └── maps entity → DTO → JSON → Browser renders
```

**B. Ingestion flow (runs on a timer, every 8h):**

```
@Scheduled fires → for each Disease in DB → build source query →
  HTTP GET external API → parse JSON → upsert Trial/Publication →
  if new record → create an Event → save. Frontend picks it up on next read.
```

### 1.4 The "Event" as the core abstraction

Every meaningful change (a new trial, a new paper) is recorded as an **Event** — a small, human-readable
row with a `summary`, an `impactScore` (1–10), and a pointer back to the thing that changed
(`refType` + `refId`). The Event Feed is just these rows, newest first. This is what turns raw source
data into "what changed and why it matters."

### 1.5 Technology stack

| Layer      | Technology                                                                 |
|------------|----------------------------------------------------------------------------|
| Frontend   | React 18, TypeScript 5, Vite 5, React Router 6 (no state library, no UI kit)|
| Backend    | Java 21, Spring Boot 3.3.0 (Web, Data JPA, Validation), Maven               |
| Data access| Spring Data JPA / Hibernate                                                 |
| Migrations | Flyway (`flyway-core` + `flyway-database-postgresql`)                       |
| HTTP client| `RestTemplate`                                                             |
| JSON       | Jackson (with JSR-310 java.time module)                                     |
| Database   | PostgreSQL                                                                  |
| Backend host | Render (Docker image via `render.yaml`) — Oracle VM path also provided (`deploy.sh`) |
| Frontend host| Cloudflare Pages (static build)                                          |

---

## 2. Low-Level Design (LLD)

### 2.1 Package / directory layout

```
backend/src/main/java/com/bdit/tracker/
├── BiotechTrackerApplication.java     @SpringBootApplication + @EnableScheduling (entry point)
├── config/
│   └── AppConfig.java                 RestTemplate, ObjectMapper, CORS beans
├── controller/
│   ├── DiseaseController.java         /api/diseases** — list, detail, trials, papers, events
│   ├── EventController.java           /api/events    — global event feed
│   ├── AdminController.java           /api/admin/ingest — manual ingestion trigger (POST)
│   └── GlobalExceptionHandler.java    NoSuchElementException → HTTP 404
├── service/
│   ├── DiseaseService.java            list/find diseases, compute trial/paper counts
│   ├── TrialService.java              paged trials per disease → DTO
│   ├── PublicationService.java        paged publications per disease → DTO
│   └── EventService.java              paged events (global + per disease) → DTO
├── ingestion/
│   ├── ClinicalTrialsIngestionJob.java  pulls trials from clinicaltrials.gov
│   └── PubMedIngestionJob.java          pulls papers from PubMed E-utilities
├── model/                            JPA entities: Disease, Company, Trial, Publication, Event
├── repository/                       Spring Data JPA repositories (one per aggregate)
└── dto/                              Java records: DiseaseDTO, TrialDTO, PublicationDTO, EventDTO, PagedResponse
backend/src/main/resources/
├── application.properties            config (DB, CORS, ingestion cron, flyway)
└── db/migration/
    ├── V1__init_schema.sql           tables + indexes + seed (ALS, Alzheimer's, Pancreatic Cancer)
    ├── V2__add_ankylosing_spondylitis.sql
    ├── V3__add_brain_cancer_and_parkinsons.sql
    └── V4__add_pulmonary_and_liver_diseases.sql   IPF, COPD, MASH, PSC

frontend/src/
├── main.tsx / App.tsx                Router with 3 routes
├── services/api.ts                   typed fetch client + BASE URL switch (dev proxy vs prod)
├── pages/
│   ├── Dashboard.tsx                 grid of disease cards
│   ├── DiseaseDetail.tsx             tabs: Trials / Papers / Events
│   └── EventFeed.tsx                 global feed with per-disease filter chips
└── components/                       Nav, Paginator, PhaseBadge, StatusBadge
```

### 2.2 Data model (schema)

Defined in `V1__init_schema.sql`, mapped by the JPA entities in `model/`.

| Table         | Key columns                                                                                       | Notes |
|---------------|---------------------------------------------------------------------------------------------------|-------|
| `disease`     | `id`, `name` (unique), `slug` (unique)                                                             | The controlled list. Seeded by migrations. |
| `company`     | `id`, `name` (unique)                                                                              | Sponsor/company; table exists, not yet populated by ingestion. |
| `trial`       | `id`, `disease_id` FK, `company_id` FK (nullable), `nct_id` (unique), `title`, `phase`, `status`, `sponsor`, `start_date`, `last_updated`, `source_url` | `nct_id` is the natural key used for upsert. |
| `publication` | `id`, `disease_id` FK, `pubmed_id` (unique), `title`, `abstract`, `authors`, `journal`, `published_date`, `source_url` | `pubmed_id` is the natural key used for dedupe. |
| `event`       | `id`, `disease_id` FK, `event_type`, `summary`, `impact_score` (default 5), `ref_type`, `ref_id`, `created_at` | The activity stream. `ref_type`/`ref_id` point back to a trial/publication. |

Indexes: `trial(disease_id)`, `publication(disease_id)`, `event(disease_id)`, `event(created_at DESC)`.

Relationships: `Trial` and `Event` each `@ManyToOne` → `Disease` (lazy). `Trial` `@ManyToOne` → `Company` (lazy, nullable).

> **Naming gotcha:** the `abstract` column maps to the Java field `abstrakt` in the `Publication`
> entity/DTO because `abstract` is a reserved Java keyword. The JSON field is therefore `abstrakt`.

### 2.3 Backend layers in detail

#### Entry point — `BiotechTrackerApplication`
`@SpringBootApplication` + `@EnableScheduling`. The `@EnableScheduling` is what makes the ingestion
jobs' `@Scheduled` cron triggers fire.

#### Config — `AppConfig`
- `restTemplate()` — the plain HTTP client both ingestion jobs use.
- `objectMapper()` — Jackson configured with `JavaTimeModule`, dates as ISO strings (not timestamps),
  and `FAIL_ON_UNKNOWN_PROPERTIES` disabled (external APIs send many fields we ignore).
- `corsConfigurer()` — allows the Cloudflare Pages origin to call `/api/**`; **GET + OPTIONS only**
  (the API is read-only to the public). Origins come from `app.cors.allowed-origins`.

#### Controllers (Web layer)
All endpoints are `GET` and return DTOs/`PagedResponse` serialized to JSON, except the one admin `POST`.

| Method & path                          | Controller method                    | Delegates to |
|----------------------------------------|--------------------------------------|--------------|
| `GET /api/diseases`                     | `DiseaseController.listDiseases`     | `DiseaseService.findAll` |
| `GET /api/diseases/{slug}`              | `DiseaseController.getDisease`       | `DiseaseService.findBySlug` |
| `GET /api/diseases/{slug}/trials`       | `DiseaseController.getTrials`        | `DiseaseService.getEntityBySlug` → `TrialService.findByDiseaseId` |
| `GET /api/diseases/{slug}/papers`       | `DiseaseController.getPapers`        | `DiseaseService.getEntityBySlug` → `PublicationService.findByDiseaseId` |
| `GET /api/diseases/{slug}/events`       | `DiseaseController.getEvents`        | `DiseaseService.getEntityBySlug` → `EventService.findByDiseaseId` |
| `GET /api/events`                       | `EventController.listEvents`         | `EventService.findAll` |
| `POST /api/admin/ingest`                | `AdminController.triggerIngestion`   | spawns a thread running both ingestion jobs |

`GlobalExceptionHandler` turns a missing slug (`NoSuchElementException` thrown in `DiseaseService`)
into a clean `404` with `{"error": "..."}`.

#### Services (business layer)
- `DiseaseService` — `findAll`/`findBySlug` map `Disease` → `DiseaseDTO`, and crucially compute
  `trialCount` and `publicationCount` per disease via `countByDiseaseId` (these power the dashboard cards).
  `getEntityBySlug` resolves a slug to the entity (or throws) so controllers can pass an id downstream.
- `TrialService` / `PublicationService` / `EventService` — each takes `(diseaseId, page, size)`, calls
  the matching sorted repository method, and wraps the JPA `Page` into a `PagedResponse<DTO>`.
  Sorting: trials by `last_updated desc`, papers by `published_date desc`, events by `created_at desc`.

#### Repositories (persistence)
Spring Data JPA interfaces — method names generate the SQL:
- `DiseaseRepository`: `findBySlug`, `findByName`.
- `TrialRepository`: `findByDiseaseIdOrderByLastUpdatedDesc`, `findByNctId` (upsert lookup), `countByDiseaseId`.
- `PublicationRepository`: `findByDiseaseIdOrderByPublishedDateDesc`, `findByPubmedId` (dedupe), `countByDiseaseId`.
- `EventRepository`: `findByDiseaseIdOrderByCreatedAtDesc`, `findAllByOrderByCreatedAtDesc`, plus a
  `...CreatedAtAfter...` finder available for future "last 24–72h" queries.

#### DTOs
Immutable Java `record`s — `DiseaseDTO`, `TrialDTO`, `PublicationDTO`, `EventDTO`, and the generic
`PagedResponse<T>(content, page, size, totalElements, totalPages)`. DTOs exist so the API never leaks
JPA entities / lazy proxies and the JSON shape stays stable.

### 2.4 The ingestion pipeline (the heart of the system)

Both jobs follow the same shape and are the only writers to the database.

**`ClinicalTrialsIngestionJob`** — cron `0 0 */8 * * *` (every 8h, on the hour)
1. `@Scheduled` fires (guarded by `app.ingestion.enabled`).
2. Load **all** diseases from the DB (`diseaseRepository.findAll()`).
3. For each disease, look up its source query in the in-code `DISEASE_QUERIES` map
   (e.g. `"Brain Cancer" → "Brain+Neoplasms"`). Diseases with no mapping are skipped.
4. `GET https://clinicaltrials.gov/api/v2/studies?query.cond=...` (50 most-recently-updated studies).
5. Parse each study's `protocolSection` with Jackson; extract NCT id, title, status, phase, sponsor, start date.
6. **Upsert** by `nctId` (`findByNctId` → update existing or create new).
7. If the trial is new, emit a `TRIAL_ADDED` **Event** (impact 5) via the shared `createEvent` helper.

**`PubMedIngestionJob`** — cron `0 30 */8 * * *` (every 8h, at :30, staggered from trials)
1–3. Same disease loop + `DISEASE_QUERIES` map (e.g. `"parkinson+disease+therapy"`).
4. **esearch** → get up to 20 recent PubMed IDs for the query.
5. **esummary** → fetch article summaries for those IDs in one call.
6. Skip IDs already stored (`findByPubmedId`); otherwise create a `Publication`.
7. Emit a `PAPER_PUBLISHED` **Event** (impact 4).

Both parse dates defensively with a list of `DateTimeFormatter`s (`parseFlexibleDate`) because the
sources use inconsistent date formats, and both swallow per-record parse errors so one bad row can't
abort the batch. Failures per disease are caught and logged, not fatal.

**Two source quirks worth knowing before you edit a query:**

- **NCBI rate limit.** E-utilities allow 3 req/s from an un-keyed IP. Each disease costs two calls, so
  an unthrottled sweep returns `429` and silently drops whole diseases — they just end up with zero
  publications, with only a logged error. `PubMedIngestionJob.throttle()` spaces calls by 400ms.
  If the disease list grows a lot, register a free NCBI API key (raises the limit to 10 req/s).
- **`RestTemplate` re-encodes String URLs.** `ClinicalTrialsIngestionJob` passes `URI.create(url)`
  rather than the `String` overload, because the `String` path escapes a pre-encoded `%22` into
  `%2522`. That failure is silent too — the API returns zero studies rather than an error. Any
  `DISEASE_QUERIES` value using `%`-encoding depends on this.

Query terms are matched loosely by ClinicalTrials.gov, so a multi-word condition can pull in unrelated
trials (unquoted, `Chronic Obstructive Pulmonary Disease` matched graft-versus-host and leukaemia
studies). Wrapping the phrase in `%22…%22` restores precision where it matters.

> **To add a disease you touch exactly three places:** (1) a new Flyway migration inserting the
> `disease` row, (2) the `DISEASE_QUERIES` map in `ClinicalTrialsIngestionJob`, (3) the same map in
> `PubMedIngestionJob`. Everything downstream is data-driven off `diseaseRepository.findAll()`,
> including the dashboard subtitle and the event-feed filter chips — no frontend change needed.
>
> The map key **must match the `disease.name` column byte-for-byte**; a mismatch is silent — the
> job's `if (query == null) continue;` just skips that disease and it stays permanently empty.
> Both maps use `Map.ofEntries(...)`, so there is no entry-count ceiling to worry about.

### 2.5 Frontend

- **`services/api.ts`** — single typed fetch wrapper. `BASE` is `/api` in dev (Vite proxy) and a
  hardcoded `https://biotech-tracker-kza2.onrender.com/api` in a production build (`import.meta.env.PROD`).
  Exposes `getDiseases`, `getDisease`, `getTrials`, `getPapers`, `getDiseaseEvents`, `getAllEvents`.
- **`App.tsx`** — three routes: `/` (Dashboard), `/disease/:slug` (DiseaseDetail), `/events` (EventFeed).
- **`Dashboard.tsx`** — fetches `getDiseases()`, renders a clickable card per disease showing trial &
  publication counts (from `DiseaseDTO`). This is why counts are computed server-side.
- **`DiseaseDetail.tsx`** — tabbed view (Trials / Papers / Events), each tab paginated via the
  matching API call and the shared `Paginator`.
- **`EventFeed.tsx`** — global feed (`getAllEvents`) with client-side per-disease filter chips; high
  `impactScore` events get a visual emphasis class.
- **`components/`** — `Nav`, `Paginator`, and `PhaseBadge`/`StatusBadge` (colour-code trial phase/status).

### 2.6 Request lifecycle example (end to end)

User clicks the "Parkinson's Disease" card → opens `/disease/parkinsons`:

```
DiseaseDetail mounts
  → api.getDisease('parkinsons')            GET /api/diseases/parkinsons
      → DiseaseController.getDisease
        → DiseaseService.findBySlug
          → DiseaseRepository.findBySlug     SELECT ... WHERE slug = 'parkinsons'
          → counts via TrialRepository/PublicationRepository.countByDiseaseId
        → DiseaseDTO → JSON
  → api.getTrials('parkinsons', 0, 20)       GET /api/diseases/parkinsons/trials?page=0&size=20
      → DiseaseController.getTrials
        → DiseaseService.getEntityBySlug (slug → id)
        → TrialService.findByDiseaseId
          → TrialRepository.findByDiseaseIdOrderByLastUpdatedDesc
        → PagedResponse<TrialDTO> → JSON
  → React renders the trials table
```

---

## 3. Deployment topology

```
Cloudflare Pages  ──HTTPS──▶  Render web service (Docker)  ──▶  PostgreSQL
 (static SPA build)            biotech-tracker.jar               (managed / VM-hosted)
```

- **Backend image** built by `Dockerfile` (multi-stage: Maven build → `eclipse-temurin:21-jre-alpine`,
  runs with `-Xmx256m`, exposes `8080`). `render.yaml` declares the web service and the env vars
  (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `CORS_ORIGINS`, `INGESTION_ENABLED`).
- **Alternative host:** `deploy.sh` runs the same JAR directly on an Oracle Cloud Always-Free VM
  (`java -jar biotech-tracker.jar`) — matches the original spec's free-tier target.
- **Frontend** is a static Vite build deployed to Cloudflare Pages; it calls the Render backend URL
  baked in at build time.
- **Schema** is applied automatically by Flyway on startup (`spring.flyway.enabled=true`), with
  Hibernate in `validate` mode (it never auto-alters tables — migrations are the single source of truth).

### 3.1 Configuration (`application.properties`, all env-overridable)

| Property | Env var | Default | Purpose |
|----------|---------|---------|---------|
| `spring.datasource.url` | `DB_URL` | `jdbc:postgresql://localhost:5432/bdit` | DB connection |
| `spring.datasource.username/password` | `DB_USERNAME` / `DB_PASSWORD` | `bdit` / `bdit` | DB creds |
| `app.cors.allowed-origins` | `CORS_ORIGINS` | `localhost:5173,4173` | allowed SPA origins |
| `app.ingestion.enabled` | `INGESTION_ENABLED` | `true` | master switch for jobs |
| `app.ingestion.cron` | `INGESTION_CRON` | `0 0 */8 * * *` | trials job schedule |
| `server.port` | `PORT` | `8080` | HTTP port |

---

## 4. Design principles & deliberate non-goals

**Principles:** one deployable artifact; read-only public API; DTO boundary; data-driven disease list;
Events as the unifying "what changed" abstraction; migrations as the schema source of truth.

**Deliberately out of scope (per the MVP spec):** Kafka, microservices, Kubernetes, Elasticsearch,
event streaming, and authentication. This is intentionally a simple monolith optimised for *clarity
of disease progress*, not data volume or scale.

## 5. Known gaps / future work
- `company` table and `Trial.company` FK exist but ingestion doesn't yet populate companies (only the
  free-text `sponsor` is stored).
- FDA approvals source is in the product spec but not yet implemented as a job.
- `Publication.abstrakt` is not populated by the current esummary call (would need an efetch call).
- `impactScore` is a fixed constant per event type; the optional AI summarization/scoring layer is not built.
- The production API URL is hardcoded in `api.ts`; consider a build-time env var.
