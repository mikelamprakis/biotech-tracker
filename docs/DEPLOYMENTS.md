# Deployments

Where everything runs, what its URL is, and how to redeploy it.

Companion to [`ARCHITECTURE.md`](./ARCHITECTURE.md) — that document explains *how the system is built*;
this one explains *where it lives*.

---

## 1. Live URLs

| Component | URL | Host | Plan |
|-----------|-----|------|------|
| **Frontend (the app)** | **https://biotech-tracker.pages.dev** | Cloudflare Pages | Free |
| Backend API | https://biotech-tracker-kza2.onrender.com/api | Render (Docker web service) | Free |
| Health check | https://biotech-tracker-kza2.onrender.com/api/health | Render | Free |
| Keep-alive Worker | `biotech-tracker-keepalive` (Workers dashboard) | Cloudflare Workers | Free |
| Database | *internal only — not publicly exposed* | PostgreSQL (connection string in Render env) | Free |
| Source | https://github.com/mikelamprakis/biotech-tracker | GitHub | — |

Total running cost: **£0/month**, per the MVP cost constraint.

> **Note:** the Pages URL is not referenced anywhere in the code — the frontend never needs to know its
> own address. It is, however, the value of `CORS_ORIGINS` on Render, which is the authoritative record
> of it. If you ever lose it, a preflight against the API will confirm a candidate:
> ```bash
> curl -sI -X OPTIONS -H "Origin: https://biotech-tracker.pages.dev" \
>   -H "Access-Control-Request-Method: GET" \
>   https://biotech-tracker-kza2.onrender.com/api/health | grep -i access-control-allow-origin
> ```
> An allowed origin is echoed back; anything else returns `403`.

---

## 2. Topology

```text
        Browser
           │
           ▼
  Cloudflare Pages ──────HTTPS/CORS──────▶  Render web service  ──JDBC──▶  PostgreSQL
  (static Vite SPA)                          (Docker, Spring Boot)
  biotech-tracker.pages.dev                  biotech-tracker-kza2.onrender.com
                                                      ▲
                                                      │ GET /api/health every 10 min
                                             Cloudflare Worker (cron)
                                             biotech-tracker-keepalive
```

Three independent deploys. Nothing is coupled at deploy time — the frontend and backend are released
separately, and the Worker only needs redeploying if the backend URL changes.

---

## 3. Backend — Render

**Service:** `biotech-tracker-backend` → `https://biotech-tracker-kza2.onrender.com`

Built from the **repo-root `Dockerfile`** (multi-stage: `maven:3.9-eclipse-temurin-21-alpine` builds the
JAR → `eclipse-temurin:21-jre-alpine` runs it with `-Xmx256m`, exposing `8080`). `render.yaml` declares
the service as `runtime: image` with `dockerfilePath: ./Dockerfile`.

The Dockerfile lives at the repo root rather than in `backend/` because Render's build context is the
repository root — hence its `COPY backend/pom.xml` / `COPY backend/src` paths.

### Deploy

Push to `main`. Render auto-builds from the connected GitHub repo. To force one without a code change,
use **Manual Deploy → Deploy latest commit** in the Render dashboard.

### Environment variables

Set in the Render dashboard, **not** in the repo — `render.yaml` marks them `sync: false` so no secret
is ever committed.

| Variable | Value | Notes |
|----------|-------|-------|
| `DB_URL` | `jdbc:postgresql://…` | PostgreSQL JDBC connection string |
| `DB_USERNAME` | *(secret)* | |
| `DB_PASSWORD` | *(secret)* | |
| `CORS_ORIGINS` | `https://biotech-tracker.pages.dev` | Comma-separated; **must** match the Pages URL exactly or the app gets CORS errors |
| `INGESTION_ENABLED` | `true` | Master switch for the scheduled jobs |
| `PORT` | *(injected by Render)* | Defaults to `8080` locally |

Every one of these has a local-dev default in `application.properties`, so nothing is required to run
the backend on your machine. See `.env.example`.

### Free-tier behaviour

Render free web services **spin down after ~15 minutes of inactivity**, and a cold start takes tens of
seconds. Worse, the spun-down JVM stops the `@Scheduled` ingestion jobs entirely. The keep-alive Worker
(§5) exists to prevent this.

### Schema migrations

Flyway runs automatically on startup (`spring.flyway.enabled=true`), applying
`backend/src/main/resources/db/migration/V*.sql` in order. Hibernate is in `validate` mode and never
alters tables — **migrations are the only way the schema changes**. To add a disease or change a table,
add a new `V{n}__description.sql` and deploy; there is no separate migration step.

### Manual ingestion trigger

Ingestion normally runs on `INGESTION_CRON` (`0 0 */8 * * *` — every 8 hours). To force a run:

```bash
curl -X POST https://biotech-tracker-kza2.onrender.com/api/admin/ingest
```

It returns `{"status":"ingestion started"}` immediately and does the work on a background thread, so
the response tells you nothing about the outcome. **Budget ~4–5 minutes for a full sweep** — Render's
free tier gives ~0.1 CPU, so each disease takes roughly 20 seconds against ~0.4s on a laptop. Diseases
are processed in `disease.id` order, so the most recently added ones populate *last*; don't conclude a
new disease has failed until the whole run has had time to finish. Watch progress with:

```bash
curl -s https://biotech-tracker-kza2.onrender.com/api/diseases | python3 -m json.tool
```

⚠️ This endpoint is **unauthenticated** (the MVP has no auth by design). It only pulls from public
sources and is idempotent — records upsert on `nct_id` / `pubmed_id` — so the blast radius is low, but
it is publicly callable. Worth putting behind a shared secret if the project grows.

---

## 4. Frontend — Cloudflare Pages

**Site:** https://biotech-tracker.pages.dev

A static Vite + React SPA. The build settings live in the Cloudflare Pages dashboard, not in the repo.
Below is what the repo requires — worth checking these match the dashboard if a build ever misbehaves:

| Setting | Value |
|---------|-------|
| Build command | `npm run build` (`tsc && vite build`) |
| Build output directory | `dist` |
| Root directory | `frontend` |
| Node version | 18+ (Vite 5 / React 18) |

### Deploy

Push to `main` — Pages auto-builds from the connected GitHub repo. Or manually:

```bash
cd frontend
npm ci
npm run build          # → frontend/dist
npx wrangler pages deploy dist --project-name=biotech-tracker
```

### API URL configuration — read this before changing hosts

The production API URL is **hardcoded** in `frontend/src/services/api.ts`:

```ts
const BASE = import.meta.env.PROD
  ? 'https://biotech-tracker-kza2.onrender.com/api'
  : '/api'
```

In dev, `/api` is proxied to `localhost:8080` by `vite.config.ts`. In production the literal above is
baked into the bundle at build time.

`frontend/.env.production` also sets `VITE_API_URL` to the same value, but **it is currently unused** —
the hardcoded ternary wins. If you move the backend, edit `api.ts` (updating only the env file will
silently do nothing). Cleaning this up is listed as a known gap in `ARCHITECTURE.md` §5.

---

## 5. Keep-alive Worker — Cloudflare Workers

**Worker:** `biotech-tracker-keepalive` (see `keepalive-worker/`)

A cron-triggered Worker that pings `/api/health` every 10 minutes so Render's free tier never spins the
backend down — which also keeps the 8-hourly ingestion firing on time. The ping *must* come from an
external always-on source: once Render sleeps the service, the JVM and its internal scheduler are
stopped, so the backend cannot wake itself.

Config in `keepalive-worker/wrangler.toml`:

```toml
[triggers]
crons = ["*/10 * * * *"]        # 10 min, comfortably under the ~15 min spin-down window

[vars]
HEALTH_URL = "https://biotech-tracker-kza2.onrender.com/api/health"
```

### Deploy

```bash
cd keepalive-worker
npm install
npx wrangler login       # first time only
npm run deploy           # publishes the Worker and registers the cron trigger
```

### Observe

```bash
npm run tail             # live-stream Worker logs
```

The Worker also exposes a `fetch` handler — hitting its URL in a browser runs the same ping on demand
and returns the result as JSON. Cost is ~144 invocations/day against a 100k/day free tier.

**If the backend URL changes,** update `HEALTH_URL` and redeploy, or the Worker will keep pinging a dead
address and the backend will start sleeping again.

---

## 6. Database

PostgreSQL, reached over JDBC from the Render service. The connection string lives only in Render's
`DB_URL` env var — it is not in the repo and the database is not publicly exposed.

Schema is owned by Flyway (§3). Four migrations exist today: `V1__init_schema.sql`,
`V2__add_ankylosing_spondylitis.sql`, `V3__add_brain_cancer_and_parkinsons.sql`,
`V4__add_pulmonary_and_liver_diseases.sql` — seeding 10 tracked diseases in total.

---

## 7. Alternative backend host — Oracle Cloud VM (not in use)

`deploy.sh` at the repo root runs the same JAR directly on an Oracle Cloud Always-Free VM
(`java -jar biotech-tracker.jar`), matching the original spec's free-tier target. It is **not currently
used** — the live backend is on Render.

Two things to fix before it would work: its `CORS_ORIGINS` default is still the placeholder
`https://your-cloudflare-pages-url`, and it assumes a local PostgreSQL at
`jdbc:postgresql://localhost:5432/bdit`.

---

## 8. Runbook

### Check everything is up

```bash
curl -s https://biotech-tracker-kza2.onrender.com/api/health   # {"status":"UP","timestamp":"…"}
curl -sI https://biotech-tracker.pages.dev | head -1           # HTTP/2 200
```

### "The site loads but shows no data"

Almost always CORS or a cold backend.

1. Open the browser console. A CORS error means `CORS_ORIGINS` on Render doesn't exactly match the
   Pages origin (scheme, no trailing slash). Fix it in the Render dashboard and restart the service.
2. Hit `/api/health` directly. A slow first response means the service was asleep — check the Worker
   with `wrangler tail` from `keepalive-worker/`.
3. Confirm the frontend is calling the right host: the API base is compiled into the bundle, so a stale
   build can point at an old URL. Rebuild and redeploy after changing `api.ts`.

### "Data is stale"

Ingestion runs every 8 hours. Check `INGESTION_ENABLED=true` on Render, then force a run with
`POST /api/admin/ingest` (§3) and watch the Render logs.

### Moving the backend to a new URL

All three places must change, or something breaks silently:

1. `frontend/src/services/api.ts` — the hardcoded prod URL (and `frontend/.env.production` for tidiness)
2. `keepalive-worker/wrangler.toml` — `HEALTH_URL`, then `npm run deploy`
3. Render `CORS_ORIGINS` — only if the *frontend* origin changed
4. This document

---

## 9. Repo → deployment map

| Path | Deploys to |
|------|-----------|
| `Dockerfile` (repo root) | Render backend image — **this is the one Render builds** |
| `render.yaml` | Render service definition |
| `backend/` | Spring Boot source built into the image |
| `frontend/` | Cloudflare Pages site |
| `keepalive-worker/` | Cloudflare Worker |
| `deploy.sh` | Oracle VM path (unused) |

> **Gotcha:** an untracked `backend/Dockerfile` may exist in your working tree. It is a near-duplicate
> of the root one differing only in `COPY` paths, and **Render does not use it**. Editing it has no
> effect on production. Delete it or commit it deliberately — leaving it around invites confusion.
