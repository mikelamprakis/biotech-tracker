/**
 * Keep-alive Worker for the Biotech Tracker backend.
 *
 * The Render free tier spins the backend down after ~15 minutes of inactivity.
 * This Worker's cron trigger (see wrangler.toml) pings the lightweight
 * /api/health endpoint every 10 minutes so the service stays warm — which also
 * keeps the scheduled 8-hourly ingestion jobs firing on time.
 *
 * A self-ping from inside the backend can't do this: when the service sleeps,
 * its JVM (and its internal scheduler) is stopped, so the ping must come from
 * an external, always-on source like this Worker.
 */

async function ping(env) {
  const url = env.HEALTH_URL;
  const start = Date.now();
  try {
    // 20s timeout so a cold start (which is exactly what we're preventing) or a
    // hung backend can't wedge the invocation.
    const resp = await fetch(url, {
      method: "GET",
      signal: AbortSignal.timeout(20_000),
    });
    const ms = Date.now() - start;
    const msg = `[keepalive] GET ${url} -> ${resp.status} in ${ms}ms`;
    console.log(msg);
    return { ok: resp.ok, status: resp.status, ms, msg };
  } catch (err) {
    const ms = Date.now() - start;
    const msg = `[keepalive] GET ${url} -> ERROR after ${ms}ms: ${err}`;
    console.error(msg);
    return { ok: false, status: 0, ms, msg };
  }
}

export default {
  // Fired by the cron trigger.
  async scheduled(event, env, ctx) {
    ctx.waitUntil(ping(env));
  },

  // Optional: lets you test the ping manually by visiting the Worker URL.
  async fetch(request, env, ctx) {
    const result = await ping(env);
    return new Response(JSON.stringify(result, null, 2), {
      status: result.ok ? 200 : 502,
      headers: { "content-type": "application/json" },
    });
  },
};
