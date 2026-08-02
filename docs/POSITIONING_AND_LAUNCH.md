# Positioning & Launch Playbook

How to frame the Biotech Disease Intelligence Tracker and announce it on social media.
Currently tracks 10 diseases: **ALS, Alzheimer's, Pancreatic Cancer, Ankylosing Spondylitis, Brain Cancer,
Parkinson's Disease, Idiopathic Pulmonary Fibrosis, COPD, MASH (metabolic dysfunction-associated
steatohepatitis), Primary Sclerosing Cholangitis.**

---

> ### ⚠️ Claim discipline — read before using any copy below
>
> **Plain-English summarisation is a planned feature, not a shipped one.** Today an event reads
> `"New publication: Pruritus is common in primary sclerosing cholangitis, persists over time…"` —
> the raw source title with a prefix. `impactScore` is a hardcoded constant (5 for trials, 4 for
> papers), so nothing is actually ranked by importance either. See `ARCHITECTURE.md` §5.
>
> Until the summarisation layer ships, **do not claim** "plain English", "no PhD required",
> "explained in one line", "why it matters", or "the biggest updates this week". Claim
> *aggregation, freshness and provenance* — all three are true and all three are useful.
>
> Everything in §9 (Roadmap) is what unlocks the stronger claims. When it ships, this warning
> comes out and the plain-English framing goes back in.

## 1. The one-liner

Lead with the *outcome*, not the tech. A few interchangeable framings, strongest first:

- **"See what's changing in research for 10 diseases — new trials and papers, in one place, refreshed daily."**
- "Clinical-trial and research activity for the diseases people care about, gathered in one feed with a link to every source."
- "ClinicalTrials.gov and PubMed, sorted by disease and by date."

Pick one and use it *everywhere* (site header, bios, first line of every post). Consistency is the point.

*(Held back until summarisation ships: "…in plain English", "no PhD required", "what changed and
why it matters".)*

## 2. What it is / what it is not

| It **is** | It is **not** |
|-----------|---------------|
| An aggregation layer over public data (ClinicalTrials.gov, PubMed) | A source of medical advice |
| A per-disease feed of new trials and papers, refreshed every 8h | A diagnosis or treatment tool |
| Free, public, no login | A place to enroll in trials |
| Structured, deduplicated, always linked to the primary source | A replacement for talking to a doctor |
| *(planned)* Plain-language summaries of each update | A translator of medical jargon — **not yet** |

> **Say this out loud in the product and the launch:** *"Educational/informational only. Not medical
> advice. Always consult a qualified clinician."* Health-adjacent products live or die on trust — a
> visible disclaimer and links back to the primary source on every item are features, not fine print.

## 3. Who it's for (and the hook for each)

1. **Patients & families** tracking a specific disease — *"Everything new in [disease] research this
   month, in one place, with a link to each source."* Your most emotionally invested, most shareable
   audience — and the one most damaged by an overclaim, so keep this hook literal.
2. **Patient-advocacy communities & nonprofits** — they need to keep members informed; you do it for them.
3. **Biotech / pharma / investors** — *"A fast pulse-check on the trial landscape per indication."*
4. **Science journalists & communicators** — a jumping-off point with primary-source links attached.

## 4. Messaging pillars (the 3 things to repeat)

1. **One place** — "Trials and research for a disease, gathered from two separate public databases into one feed."
2. **Freshness** — "Automatically refreshed every 8 hours — new trials and papers show up on their own."
3. **Trust** — "Straight from ClinicalTrials.gov and PubMed, every item links back to the source."

*(Pillar 1 becomes **Clarity** — "complex updates, translated" — once summarisation ships. It is the
single upgrade that most improves the pitch, which is why it leads the roadmap in §9.)*

## 5. Naming & tagline options

If you want a friendlier public name than "Biotech Disease Intelligence Tracker":

- **TrialPulse** — "The pulse of disease research."
- **ClearCure** *(careful — avoid implying a cure; maybe too strong for a health product)*
- **BioSignal** — "Signal, not noise, in disease research."
- **What's Changing** — literal, human, matches the product's core question.

Recommendation: a short brandable name + the honest tagline *"Disease research, in plain English."*

## 6. Social media announcement copy

> Fill the bracketed bits, attach a 10–15s screen-capture GIF of the dashboard → a disease detail →
> the event feed. Motion outperforms static screenshots on every platform below.

### X / Twitter — launch thread

**Tweet 1 (hook):**
> I built a free tracker for what's changing in research across 10 diseases — ALS, Alzheimer's,
> Parkinson's, pancreatic & brain cancer, ankylosing spondylitis, IPF, COPD, MASH, and primary
> sclerosing cholangitis. 🧵

**Tweet 2 (problem):**
> The data is public — ClinicalTrials.gov, PubMed — but it's two separate systems built for
> researchers running queries. "What's new in [disease] this month?" is weirdly hard to answer.
> So I made it answerable.

**Tweet 3 (how it works):**
> Every 8 hours it pulls new trials and papers, sorts them by disease, and puts them in one feed.
> Every item links straight back to the primary source. No login, nothing to buy.

**Tweet 4 (the build):**
> Runs at £0/month — Spring Boot + React on free tiers, with a Cloudflare Worker cron keeping the
> backend awake.

**Tweet 5 (CTA):**
> 👉 [link]
> Which disease should I add next? Reply below.
> *(Informational only — not medical advice.)*

### LinkedIn — single post

> I kept hitting the same wall: there's no simple way to see what changed recently in research for one
> specific disease.
>
> ClinicalTrials.gov and PubMed are both excellent and both public. But they're separate systems built
> for researchers running queries — not for someone who wants to know "what's new in pancreatic cancer
> this month?"
>
> So I built something that answers that question.
>
> It pulls new clinical trials and published research for 10 diseases — ALS, Alzheimer's, Parkinson's,
> pancreatic and brain cancer, ankylosing spondylitis, idiopathic pulmonary fibrosis, COPD, MASH and
> primary sclerosing cholangitis — refreshes every 8 hours, and gives you one feed per disease. Every
> item links straight back to the source. It's currently tracking [N] trials and [N] papers.
>
> The constraint I set myself was £0/month. One Spring Boot monolith and a React SPA: Docker on Render,
> static build on Cloudflare Pages, Postgres, and a Cloudflare Worker on a cron to stop the free-tier
> backend sleeping. No Kafka, no Kubernetes, no microservices — one deployable artifact.
>
> It's an aggregator, not a translator: you get the real trial and paper titles, linked to source.
> Informational only, not medical advice.
>
> Free, no login → [link]
>
> Which disease should I add next?
>
> #biotech #healthtech #java #springboot #opendata

*Refresh `[N]` from `GET /api/diseases` on the morning you post — concrete counts outperform adjectives.*

### Facebook — for people who know you

Different audience: friends and family, not a professional or patient-advocacy crowd. Drop the tech
entirely, don't recruit strangers, and keep the limits explicit.

> For the last few weeks I've been building this in my spare time.
>
> If you've ever tried to find out what's actually happening in research for a specific illness, you'll
> know it's harder than it should be. The information is public, but it's spread across databases meant
> for scientists.
>
> So I built a site that gathers it in one place — new clinical trials and new research for 10 conditions
> including Alzheimer's, Parkinson's, ALS, COPD and pancreatic cancer — updated automatically, with a
> link to the original source for everything.
>
> It's free and there's nothing to sign up for. It won't tell you what any of it means for a specific
> person — that's a conversation for a doctor — but it will show you what's new.
>
> [link]

### Reddit — value-first, no hype (e.g. r/ALS, r/Alzheimers, r/braincancer, disease-specific subs)

> **I made a free, no-login site that gathers new [disease] trials and research into one feed**
>
> Hi all — I kept finding it hard to answer "what's new in [disease] research lately?" without wading
> through ClinicalTrials.gov and PubMed separately, so I built something that checks both automatically
> and lists what's new, with a link to the source for each item.
>
> To be upfront: it doesn't translate anything into plain English yet — you get the real trial and paper
> titles. That's the next thing I want to build, and I'd rather say so than oversell it.
>
> It's free, no account, and I'm not selling anything. It currently covers [diseases]. Would this be
> useful to you, and what would make it better?
>
> *(Informational only, not medical advice.)*
>
> Link: [link]

> **On the `[personal reason]` some templates ask for:** if you have no personal connection to these
> diseases, don't invent one. It's the most tempting move in a health-adjacent launch and the most
> damaging when it unravels — patient communities are unusually good at spotting it. *"I found this
> hard to look up and thought it should be easier"* is a complete and respectable reason.

*Read each sub's self-promotion rules first; lead with usefulness and a genuine question, not the link.*

### Product Hunt — tagline + first comment

- **Tagline:** "New clinical trials and research, per disease, updated daily."
- **First maker comment:** why you built it, what's under the hood (free-tier Spring Boot + React,
  auto-refreshing from public data), what it deliberately doesn't do yet (summarisation), and an
  explicit ask: *which diseases next?*

### Hacker News — Show HN (technical, understated)

> **Show HN: A plain-English tracker for clinical trials and research, per disease**
>
> It's a small Spring Boot monolith + React SPA that pulls from the ClinicalTrials.gov v2 API and PubMed
> E-utilities every 8 hours, normalizes each update into an "event" row, and serves a read-only JSON API
> to a static frontend. Runs entirely on free tiers (Render + Cloudflare Pages + a Worker cron to stop
> the backend sleeping). Summarisation is deliberately not built yet — right now it's aggregation and
> provenance only. Happy to talk architecture or data-quality tradeoffs.

### Instagram / TikTok / Shorts — 15-second script

> [Screen recording over voiceover]
> "If someone you love has ALS, Alzheimer's, or Parkinson's — here's a free way to see what's new in the
> research. New trials and new studies for 10 conditions, pulled from the public databases into one
> place and updated every day, with a link to every source. Link in bio."

## 7. Launch sequencing (suggested)

1. **Pre-launch:** ✅ CORS/prod URL confirmed working, ✅ real data ingested across all 10 diseases,
   ✅ medical disclaimer in the site footer on every route.
2. **Soft launch:** post to 1–2 disease communities you're part of; gather feedback; fix rough edges.
3. **Main launch:** LinkedIn + X thread same morning; Show HN / Product Hunt if you want a tech spike.
4. **Sustain:** a weekly "what's new this week in [disease]" post. Note this is currently *manual* —
   `impactScore` is a constant, so the product cannot yet rank updates for you. Pick the interesting
   ones by hand until the scoring layer in §9 ships.

## 8. Do / Don't

**Do:** link primary sources; keep the disclaimer visible; invite disease requests (cheap engagement +
roadmap signal); show motion (GIF/video); tell the honest reason you built it; say plainly what the
product doesn't do yet — "it's an aggregator, not a translator" earns more trust than it costs.

**Don't:** imply cures, breakthroughs you can't back, or medical advice; overstate "real-time" (it's
every 8h — say "updated daily"); scrape or claim data you don't have; spam communities with a bare
link; claim plain-English summarisation before §9 ships; invent a personal connection to a disease.

---

## 9. Roadmap — what unlocks the stronger pitch

In priority order by marketing value per unit of work:

1. **Plain-language summaries.** The one feature that turns "aggregator" into "clarity layer" and
   makes the whole original positioning honest. Per `ARCHITECTURE.md` §5 the hook already exists —
   `Event.summary` is generated in `createEvent`, so this is a summarisation call at ingestion time,
   not a re-architecture. Cheapest credible version: a short LLM call per new trial/paper, generated
   once and stored, so there's no per-request cost.
2. **Real `impactScore`.** Currently a constant, which makes "the biggest updates this week" a claim
   the product can't support. Even a crude heuristic (Phase 3 > Phase 1, new approval > new paper)
   would beat a constant and would power a weekly recurring post — your best sustain-content engine.
3. **FDA approvals ingestion.** In the original spec, never built. Approvals are the highest-signal,
   most shareable events in the whole domain — a single approval is worth more attention than fifty
   new trial registrations.
4. **Company/sponsor rollups.** The `company` table exists but ingestion only stores free-text
   `sponsor`. "Which companies are working on this?" is one of the four questions in the product
   spec and currently goes unanswered.

Ship 1 and 2 before the main launch if you can — they're what the strongest version of the copy
depends on. Soft-launching on aggregation alone is still perfectly honest.
