# Kalky Backend

## Project Overview
Bun + SQLite backend for the Kalky food tracking app. Provides food image analysis (via OpenAI GPT-5-mini vision), barcode product lookup, and text search. No framework — raw `Bun.serve` API with minimal dependencies.

## Architecture
- **Runtime**: Bun (built-in SQLite via `bun:sqlite`)
- **Language**: TypeScript (strict mode)
- **Database**: SQLite with WAL mode
- **AI**: OpenAI SDK (`gpt-5-mini` vision model)
- **Server**: `Bun.serve` on `0.0.0.0:${PORT}`

## Project Structure
```
backend/
├── src/
│   ├── index.ts                 # Server entry point, routing
│   ├── db/
│   │   ├── schema.ts            # DB init, connection, WAL + FK pragmas
│   │   └── products.ts          # Product queries (getByBarcode, search, insert)
│   ├── middleware/
│   │   └── auth.ts              # Admin auth middleware (Bearer token vs ADMIN_KEY)
│   ├── routes/
│   │   ├── admin.ts             # POST /api/admin/import — bulk product import
│   │   ├── analyze.ts           # POST /cal — image analysis
│   │   ├── barcode.ts           # GET /api/barcode/:code — lookup
│   │   └── search.ts            # GET /api/search?q= — text search
│   └── services/
│       └── openai.ts            # OpenAI GPT-5-mini vision, lazy singleton client
├── scripts/
│   └── import.ts                # JSON product import script
├── data/
│   └── products.sqlite          # SQLite database file
├── package.json
├── tsconfig.json
├── Dockerfile
├── .env.example
└── .gitignore
```

## Database Schema
```sql
CREATE TABLE products (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  barcode TEXT UNIQUE,
  name TEXT NOT NULL,
  energy_kcal_100g REAL DEFAULT 0,
  protein_100g REAL DEFAULT 0,
  fat_100g REAL DEFAULT 0,
  carbs_100g REAL DEFAULT 0,
  serving_size TEXT,
  image_url TEXT,
  created_at TEXT DEFAULT (datetime('now')),
  updated_at TEXT DEFAULT (datetime('now'))
);
```
Indexes: `idx_products_barcode` (barcode), `idx_products_name` (name).

## API Endpoints

### `POST /cal` — Food Image Analysis
- **Content-Type**: `image/*` (raw image bytes in body)
- **Response**: `{ weight, foodType, title, protein, fat, carbs, healthScore }`
- Title returned in Czech. All numeric values are integers.
- Requires `OPENAI_API_KEY` env var.

### `GET /api/barcode/:code` — Product Lookup
- **Response**: Product object or `404 { "error": "Product not found" }`

### `GET /api/search?q=...` — Product Search
- Case-insensitive LIKE search on product name, max 20 results.
- **Response**: Array of product objects.

### `POST /api/admin/import` — Bulk Product Import
- **Auth**: `Authorization: Bearer <ADMIN_KEY>` required
- **Body**: `{ "products": [...] }` or bare array `[...]`
- Each product requires `name` (string); `barcode`, numeric fields, `serving_size`, `image_url` are optional
- Uses SQLite transaction for batch performance; calls `insertProduct()` per product (upsert on barcode)
- **Response**: `{ "imported": N, "failed": N, "errors": [...] }`
- **Errors**: `401` (bad/missing auth), `503` (ADMIN_KEY not configured), `400` (invalid JSON/format)

### `GET /health` — Health Check
- **Response**: `{ "status": "ok" }`

### `OPTIONS *` — CORS Preflight
- Returns 204 with CORS headers.

## Environment Variables
- `OPENAI_API_KEY` — Required for `/cal` endpoint
- `PORT` — Server port (default: 3000)
- `ADMIN_KEY` — Required for `/api/admin/import` endpoint

## Build & Run
```bash
bun install                      # Install dependencies
bun run src/index.ts             # Production
bun --watch run src/index.ts     # Dev with hot reload (or: bun run dev)
```

## Import Script
```bash
bun run scripts/import.ts <path-to-json>
```
Expects JSON array of product objects with fields: `barcode`, `name`, `energy_kcal_100g`, `protein_100g`, `fat_100g`, `carbs_100g`, optional `serving_size`, `image_url`.

## CI/CD
- GitHub Actions workflow: `.github/workflows/backend.yml`
- Triggers on push to `main`/`master` and PRs touching `backend/**`
- Steps: `bun install` → `bun tsc --noEmit` (typecheck) → `docker build`

## Key Conventions
- CORS enabled on all routes (`Access-Control-Allow-Origin: *`)
- OpenAI client initialized lazily (first `/cal` request)
- `/cal` response format (`FoodAnalysisDto`) matches the Kotlin app's `FoodAnalysisClient` expectations
- Product insert uses upsert — conflicts on barcode update all fields
- Database stored in `data/products.sqlite`, auto-created on first run
