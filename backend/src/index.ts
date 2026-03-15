import { initDb } from "./db/schema";
import { handleBarcode } from "./routes/barcode";
import { handleSearch } from "./routes/search";
import { handleAnalyze } from "./routes/analyze";

const PORT = Number(process.env.PORT) || 3000;

function corsHeaders(): HeadersInit {
  return {
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Methods": "GET, POST, OPTIONS",
    "Access-Control-Allow-Headers": "Content-Type",
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

await initDb();

Bun.serve({
  port: PORT,
  async fetch(req) {
    const url = new URL(req.url);

    // CORS preflight
    if (req.method === "OPTIONS") {
      return new Response(null, { status: 204, headers: corsHeaders() });
    }

    try {
      // GET /api/barcode/:code
      const barcodeMatch = url.pathname.match(/^\/api\/barcode\/(.+)$/);
      if (barcodeMatch && req.method === "GET") {
        return withCors(handleBarcode(barcodeMatch[1]));
      }

      // GET /api/search?q=...
      if (url.pathname === "/api/search" && req.method === "GET") {
        return withCors(handleSearch(url));
      }

      // POST /cal
      if (url.pathname === "/cal" && req.method === "POST") {
        return withCors(await handleAnalyze(req));
      }

      // Health check
      if (url.pathname === "/health") {
        return withCors(Response.json({ status: "ok" }));
      }

      return withCors(Response.json({ error: "Not found" }, { status: 404 }));
    } catch (err) {
      console.error("Request error:", err);
      return withCors(
        Response.json({ error: "Internal server error" }, { status: 500 })
      );
    }
  },
});

console.log(`Kalai backend running on http://0.0.0.0:${PORT}`);
