# Biotech Disease Intelligence Tracker (MVP Spec)

## 🧠 Product Overview

Build a public web application that tracks real-time developments in:

* Amyotrophic Lateral Sclerosis (ALS)
* Alzheimer’s Disease
* Pancreatic Cancer

The goal is to provide a **simple, structured, and understandable view of biotech progress**, including:

* Clinical trials
* Drug development updates
* Research papers
* Regulatory approvals

The product should translate complex biotech updates into simple insights for non-experts and professionals.

---

## 🎯 Core Value Proposition

Users should be able to answer:

* What new trials started or changed today?
* Which companies are working on treatments?
* What phase are their drugs in?
* What recent scientific discoveries matter?
* What changed in the last 24–72 hours?

---

## 🏗️ System Architecture (MVP)

Single monolithic application.

```text
Cloudflare Pages (Frontend React)
        │
        ▼
Spring Boot Monolith (Backend)
        │
        ├── REST API layer
        ├── Scheduled data ingestion jobs
        ├── Data normalization layer
        ├── Optional AI summarization
        └── PostgreSQL access layer
        │
        ▼
PostgreSQL Database
```

---

## 🚀 Deployment Strategy (FREE TIER ONLY)

### Backend Hosting

Use:

* Oracle Cloud Always Free VM

Requirements:

* Run Spring Boot JAR directly
* Port exposed via HTTP
* Optional Nginx reverse proxy

Deployment:

```bash
java -jar biotech-tracker.jar
```

---

### Frontend Hosting

Use:

* Cloudflare Pages

Frontend:

* React (Vite or CRA)
* Static build deployed to Cloudflare Pages

---

### Database

Option A (preferred):

* PostgreSQL installed on Oracle VM

Option B:

* Supabase free tier PostgreSQL

---

## 📦 Core Modules (Spring Boot Monolith)

### 1. API Layer

Expose REST endpoints:

* GET /diseases
* GET /diseases/{id}
* GET /diseases/{id}/trials
* GET /diseases/{id}/papers
* GET /diseases/{id}/events

---

### 2. Data Ingestion Layer (Scheduled Jobs)

Runs every 6–12 hours.

Sources:

#### Clinical Trials

* [https://clinicaltrials.gov/api](https://clinicaltrials.gov/api)

#### PubMed (Research Papers)

* [https://www.ncbi.nlm.nih.gov/books/NBK25501/](https://www.ncbi.nlm.nih.gov/books/NBK25501/)

#### FDA Approvals

* [https://www.fda.gov/drugs/drug-approvals-and-databases/drug-approvals-and-databases](https://www.fda.gov/drugs/drug-approvals-and-databases/drug-approvals-and-databases)

Fetch data for:

* ALS
* Alzheimer’s Disease
* Pancreatic Cancer

Normalize into internal schema.

---

### 3. Domain Model

#### Disease

* id
* name

#### Company

* id
* name

#### Trial

* id
* disease_id
* company_id
* phase (1–4)
* status (Recruiting / Completed / etc.)
* title
* last_updated

#### Publication

* id
* disease_id
* title
* abstract
* published_date

#### Event (IMPORTANT CORE CONCEPT)

Every update becomes an event:

```json
{
  "type": "TRIAL_UPDATED",
  "disease": "ALS",
  "summary": "Phase 2 trial progressed to Phase 3",
  "impactScore": 7,
  "timestamp": "2026-06-25"
}
```

---

### 4. Optional AI Layer (later / optional)

Use LLM to:

* Summarize trial updates
* Generate “what this means” explanations
* Assign impact score (1–10)

If no API budget exists:

* Skip or pre-generate summaries manually

---

## 🌐 Frontend Requirements (React)

### Pages

#### 1. Dashboard

* List diseases
* Show:

  * number of trials
  * recent updates
  * recent papers

#### 2. Disease Detail Page

Sections:

* Clinical Trials
* Research Papers
* Events Timeline
* Companies involved

#### 3. Event Feed

* Chronological feed of updates
* Filter by disease

---

## 🔄 Data Flow

```text
Scheduler triggers job
        ↓
Fetch external APIs
        ↓
Normalize data
        ↓
Store in PostgreSQL
        ↓
Create Event objects
        ↓
Expose via REST API
        ↓
Frontend renders UI
```

---

## 🧪 MVP Scope (IMPORTANT)

Do NOT implement:

* Kafka
* Microservices
* Kubernetes
* Elasticsearch
* Complex event streaming
* Authentication

This is intentionally a **simple monolith MVP**.

---

## 🧠 Key Design Principle

> Optimize for clarity of disease progress, not raw data volume.

The system should:

* Reduce noise
* Highlight meaningful updates
* Summarize complex biotech information simply

---

## 📊 Initial Dataset Scope

Only track:

* ALS
* Alzheimer’s Disease
* Pancreatic Cancer

This keeps ingestion manageable and ensures high-quality signal.

---

## 🔔 Future Extensions (NOT MVP)

* Email alerts per disease
* User watchlists
* AI-powered “breakthrough probability score”
* More diseases (cancer expansion)
* Real-time streaming updates

---

## 💰 Cost Constraint

Target: £0/month

Only use:

* Oracle Cloud Always Free VM
* Cloudflare Pages
* Free API tiers (ClinicalTrials, PubMed, FDA)

---

## 📌 Success Criteria

MVP is successful if:

* User can view latest updates per disease
* Data refreshes automatically every 6–12 hours
* System is publicly accessible
* Updates are understandable (not raw medical jargon)

---

## 🧭 Build Philosophy

* Start simple
* One deployable artifact (Spring Boot JAR)
* No distributed systems
* No premature scaling
* Focus on data + clarity, not infrastructure complexity
