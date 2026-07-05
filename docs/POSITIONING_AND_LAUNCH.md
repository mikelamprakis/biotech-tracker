# Positioning & Launch Playbook

How to frame the Biotech Disease Intelligence Tracker and announce it on social media.
Currently tracks: **ALS, Alzheimer's, Pancreatic Cancer, Ankylosing Spondylitis, Brain Cancer, Parkinson's Disease.**

---

## 1. The one-liner

Lead with the *outcome*, not the tech. A few interchangeable framings, strongest first:

- **"See what's actually changing in the fight against ALS, Alzheimer's, Parkinson's and more — updated every day, in plain English."**
- "A live dashboard of clinical-trial and research progress for the diseases people care about — no PhD required."
- "We turn the firehose of biotech updates into a simple feed: what changed, and why it matters."

Pick one and use it *everywhere* (site header, bios, first line of every post). Consistency is the point.

## 2. What it is / what it is not

| It **is** | It is **not** |
|-----------|---------------|
| A clarity layer over public data (ClinicalTrials.gov, PubMed) | A source of medical advice |
| A daily-updated "what changed" feed per disease | A diagnosis or treatment tool |
| Free, public, no login | A place to enroll in trials |
| Aggregated, structured, plain-language | A replacement for talking to a doctor |

> **Say this out loud in the product and the launch:** *"Educational/informational only. Not medical
> advice. Always consult a qualified clinician."* Health-adjacent products live or die on trust — a
> visible disclaimer and links back to the primary source on every item are features, not fine print.

## 3. Who it's for (and the hook for each)

1. **Patients & families** tracking a specific disease — *"Get the latest on [disease] without the jargon."*
   This is your most emotionally invested, most shareable audience. Lead here.
2. **Patient-advocacy communities & nonprofits** — they need to keep members informed; you do it for them.
3. **Biotech / pharma / investors** — *"A fast pulse-check on the trial landscape per indication."*
4. **Science journalists & communicators** — a jumping-off point with primary-source links attached.

## 4. Messaging pillars (the 3 things to repeat)

1. **Clarity** — "Complex biotech updates, translated. What changed and why it matters, in one line."
2. **Freshness** — "Automatically refreshed — new trials and papers show up on their own."
3. **Trust** — "Straight from ClinicalTrials.gov and PubMed, every item links back to the source."

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
> I built a free site that tracks what's *actually* changing in the fight against ALS, Alzheimer's,
> Parkinson's, pancreatic & brain cancer, and ankylosing spondylitis.
>
> New trials and research — updated automatically, explained in plain English. 🧵

**Tweet 2 (problem):**
> The info exists — ClinicalTrials.gov, PubMed — but it's a firehose of jargon. If someone you love has
> one of these diseases, "what's new this week?" is weirdly hard to answer. So I made it answerable.

**Tweet 3 (how it works):**
> Every day it pulls new clinical trials & papers, then boils each update down to one line:
> *what changed + why it matters.* One feed per disease. Every item links back to the primary source.

**Tweet 4 (CTA):**
> It's free, no login, and open to expand to more diseases.
> 👉 [link]
> Which disease should I add next? Reply below.
> *(Informational only — not medical advice.)*

### LinkedIn — single post

> **What's actually changing in disease research this week? I built a free tool that answers that.**
>
> ClinicalTrials.gov and PubMed hold an incredible amount of progress on diseases like ALS, Alzheimer's,
> Parkinson's, pancreatic and brain cancer, and ankylosing spondylitis — but it's buried in jargon and
> scattered across databases.
>
> So I built a live dashboard that:
> • Pulls new trials & research automatically
> • Summarizes each update in one plain-English line (what changed, why it matters)
> • Links every item back to its primary source
>
> It's a simple monolith — Spring Boot + React, deployed on free tiers — and it refreshes itself on a
> schedule. Free, public, no login.
>
> 👉 [link]
>
> I'd love feedback, and I'm taking requests for which disease to add next.
>
> *Informational only — not medical advice.*
>
> #biotech #healthtech #opendata #clinicaltrials

### Reddit — value-first, no hype (e.g. r/ALS, r/Alzheimers, r/braincancer, disease-specific subs)

> **I made a free, no-login site that tracks new [disease] trials & research and explains each update in plain English**
>
> Hi all — [short personal / honest reason you built it]. I kept finding it hard to answer "what's new
> in [disease] research lately?" without wading through ClinicalTrials.gov and PubMed, so I built
> something that does it automatically and summarizes each update in one line, with a link to the source.
>
> It's free, no account, and I'm not selling anything. It currently covers [diseases]. Would this be
> useful to you, and what would make it better? Happy to add [disease] detail if it's missing.
>
> *(To be clear: it's informational only, not medical advice.)*
>
> Link: [link]

*Read each sub's self-promotion rules first; lead with usefulness and a genuine question, not the link.*

### Product Hunt — tagline + first comment

- **Tagline:** "Disease research progress, in plain English — updated daily."
- **First maker comment:** why you built it (the personal story), what's under the hood (free-tier
  Spring Boot + React, auto-refreshing from public data), and an explicit ask: *which diseases next?*

### Hacker News — Show HN (technical, understated)

> **Show HN: A plain-English tracker for clinical trials and research, per disease**
>
> It's a small Spring Boot monolith + React SPA that pulls from the ClinicalTrials.gov v2 API and PubMed
> E-utilities every 8 hours, normalizes each update into an "event" with a one-line summary, and serves
> a read-only JSON API to a static frontend. Runs entirely on free tiers. Happy to talk architecture or
> data-quality tradeoffs.

### Instagram / TikTok / Shorts — 15-second script

> [Screen recording over voiceover]
> "If someone you love has ALS, Alzheimer's, or Parkinson's — here's how to see what's changing in the
> research, without needing a science degree. New trials, new studies, explained in one line, updated
> every day. It's free. Link in bio."

## 7. Launch sequencing (suggested)

1. **Pre-launch:** confirm CORS/prod URL work, seed a few days of real ingested data so the feed isn't
   empty, add the medical disclaimer to the UI footer.
2. **Soft launch:** post to 1–2 disease communities you're part of; gather feedback; fix rough edges.
3. **Main launch:** LinkedIn + X thread same morning; Show HN / Product Hunt if you want a tech spike.
4. **Sustain:** a weekly "biggest updates this week in [disease]" post — the product generates this
   content for you (the high-`impactScore` events are literally the shortlist).

## 8. Do / Don't

**Do:** link primary sources; keep the disclaimer visible; invite disease requests (cheap engagement +
roadmap signal); show motion (GIF/video); tell the human reason you built it.

**Don't:** imply cures, breakthroughs you can't back, or medical advice; overstate "real-time" (it's
every 8h — say "updated daily"); scrape or claim data you don't have; spam communities with a bare link.
