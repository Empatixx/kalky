import { ensureDataDir, prisma } from "./db/prisma";
import { handleBarcode } from "./routes/barcode";
import { handleSearch } from "./routes/search";
import { handleAnalyze } from "./routes/analyze";
import { handleAdminImport } from "./routes/admin";
import { requireAdmin, requireAppCheck, requireAuth } from "./middleware/auth";
import { handleAuthMe } from "./routes/auth";
import { handleFcmToken } from "./routes/fcm";

const PORT = Number(process.env.PORT) || 3000;

function corsHeaders(): HeadersInit {
  return {
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Methods": "GET, POST, OPTIONS",
    "Access-Control-Allow-Headers": "Content-Type, Authorization, X-Firebase-AppCheck",
  };
}

function withCors(response: Response): Response {
  const headers = new Headers(response.headers);
  for (const [key, value] of Object.entries(corsHeaders())) {
    headers.set(key, value);
  }
  return new Response(response.body, {
    status: response.status,
    statusText: response.statusText,
    headers,
  });
}

await ensureDataDir();
// Open the connection eagerly so a config error (missing DATABASE_URL etc.)
// fails the process at startup, not on the first request. Prisma's SQLite
// driver enables WAL mode and foreign-key enforcement per connection by
// default, so the explicit PRAGMAs from the bun:sqlite era are gone.
await prisma.$connect();

Bun.serve({
  port: PORT,
  async fetch(req) {
    const url = new URL(req.url);

    // CORS preflight
    if (req.method === "OPTIONS") {
      return new Response(null, { status: 204, headers: corsHeaders() });
    }

    try {
      // POST /api/auth/me
      if (url.pathname === "/api/auth/me" && req.method === "POST") {
        const appCheckError = await requireAppCheck(req);
        if (appCheckError) return withCors(appCheckError);
        const authResult = await requireAuth(req);
        if (authResult instanceof Response) return withCors(authResult);
        return withCors(await handleAuthMe(authResult));
      }

      // POST /api/auth/fcm-token
      if (url.pathname === "/api/auth/fcm-token" && req.method === "POST") {
        const appCheckError = await requireAppCheck(req);
        if (appCheckError) return withCors(appCheckError);
        const authResult = await requireAuth(req);
        if (authResult instanceof Response) return withCors(authResult);
        return withCors(await handleFcmToken(req, authResult));
      }

      // GET /api/barcode/:code
      const barcodeMatch = url.pathname.match(/^\/api\/barcode\/(.+)$/);
      if (barcodeMatch && req.method === "GET") {
        const appCheckError = await requireAppCheck(req);
        if (appCheckError) return withCors(appCheckError);
        const authResult = await requireAuth(req);
        if (authResult instanceof Response) return withCors(authResult);
        return withCors(await handleBarcode(barcodeMatch[1]));
      }

      // GET /api/search?q=...
      if (url.pathname === "/api/search" && req.method === "GET") {
        const appCheckError = await requireAppCheck(req);
        if (appCheckError) return withCors(appCheckError);
        const authResult = await requireAuth(req);
        if (authResult instanceof Response) return withCors(authResult);
        return withCors(await handleSearch(url));
      }

      // POST /cal
      if (url.pathname === "/cal" && req.method === "POST") {
        const appCheckError = await requireAppCheck(req);
        if (appCheckError) return withCors(appCheckError);
        const authResult = await requireAuth(req);
        if (authResult instanceof Response) return withCors(authResult);
        return withCors(await handleAnalyze(req));
      }

      // POST /api/admin/import
      if (url.pathname === "/api/admin/import" && req.method === "POST") {
        const authError = requireAdmin(req);
        if (authError) return withCors(authError);
        return withCors(await handleAdminImport(req));
      }

      // Health check
      if (url.pathname === "/health") {
        return withCors(Response.json({ status: "ok" }));
      }

      return withCors(Response.json({ error: "Not found" }, { status: 404 }));
    } catch (err) {
      console.error("Request error:", err);
      return withCors(
        Response.json({ error: "Internal server error" }, { status: 500 }),
      );
    }
  },
});

console.log(`Kalky backend running on http://0.0.0.0:${PORT}`);
