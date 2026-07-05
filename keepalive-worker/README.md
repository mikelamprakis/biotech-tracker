# Keep-alive Worker

A Cloudflare Worker whose cron trigger pings the backend's `/api/health` endpoint
every 10 minutes so the Render free-tier service doesn't spin down on inactivity.

## Why external?

The backend's own `@Scheduled` jobs run inside the JVM. When Render sleeps the
service the JVM stops, so nothing internal can ping it. The wake-up signal must
come from an always-on external source — this Worker.

## Deploy (free)

```bash
cd keepalive-worker
npm install
npx wrangler login          # opens a browser to authorise your Cloudflare account
npm run deploy              # publishes the Worker + registers the cron trigger
```

That's it — the cron trigger (`*/10 * * * *`, every 10 minutes) is created
automatically from `wrangler.toml`.

## Configure

- **Target URL:** edit `HEALTH_URL` under `[vars]` in `wrangler.toml`, then redeploy.
- **Interval:** edit the `crons` array in `wrangler.toml` (kept under the ~15 min
  spin-down window; 10 min leaves margin).

## Test / observe

```bash
npm run tail                # live-stream the Worker's console logs
```

Or visit the Worker's URL in a browser — the `fetch` handler runs the same ping
on demand and returns the result as JSON.

## Cost

Cron invocations count against the Workers free tier (100k requests/day). At one
ping per 10 min that's ~144/day — negligible.
