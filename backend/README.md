# Kalai Backend

[![Backend CI](https://github.com/Empatixx/kalai/actions/workflows/backend.yml/badge.svg)](https://github.com/Empatixx/kalai/actions/workflows/backend.yml)

Food analysis and product lookup backend for the Kalai nutrition tracking app. Analyzes food images using OpenAI GPT-5-mini vision and provides barcode/search lookups against a local SQLite product database.

## Prerequisites

- [Bun](https://bun.sh/) runtime (v1.0+)
- OpenAI API key (for food image analysis)
- `ADMIN_KEY` env var (for admin import endpoint)

## Setup

```bash
cd backend
bun install

# Configure environment
cp .env.example .env
# Edit .env and set your OPENAI_API_KEY
```

## Running

```bash
# Development (hot reload)
bun run dev

# Production
bun run start
```

Server starts on `http://0.0.0.0:3000` by default. Set `PORT` in `.env` to change.

## API

### POST /cal — Analyze Food Image

Send raw image bytes to get nutritional analysis.

```bash
curl -X POST http://localhost:3000/cal \
  -H "Content-Type: image/jpeg" \
  --data-binary @photo.jpg
```

Response:
```json
{
  "weight": 200,
  "foodType": "main_course",
  "title": "Kuřecí řízek",
  "protein": 25,
  "fat": 8,
  "carbs": 15,
  "healthScore": 7
}
```

### GET /api/barcode/:code — Product Lookup

Look up a product by barcode.

```bash
curl http://localhost:3000/api/barcode/8593894600019
```

Response:
```json
{
  "id": 1,
  "barcode": "8593894600019",
  "name": "Tatranky",
  "energy_kcal_100g": 520,
  "protein_100g": 7,
  "fat_100g": 28,
  "carbs_100g": 60,
  "serving_size": "47g",
  "image_url": null,
  "created_at": "2026-03-15T10:00:00",
  "updated_at": "2026-03-15T10:00:00"
}
```

Returns `404` with `{ "error": "Product not found" }` if barcode is not in the database.

### GET /api/search?q= — Search Products

Search products by name (case-insensitive, max 20 results).

```bash
curl "http://localhost:3000/api/search?q=jogurt"
```

Response: array of product objects.

### POST /api/admin/import — Bulk Product Import

Import products in bulk. Requires admin authentication.

```bash
curl -X POST http://localhost:3000/api/admin/import \
  -H "Authorization: Bearer YOUR_ADMIN_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "products": [
      {
        "barcode": "TEST001",
        "name": "Testovací produkt",
        "energy_kcal_100g": 100,
        "protein_100g": 5,
        "fat_100g": 3,
        "carbs_100g": 15
      }
    ]
  }'
```

Accepts `{ "products": [...] }` or a bare array `[...]`. Fields `name` is required; numeric fields default to 0.

Response:
```json
{
  "imported": 1,
  "failed": 0,
  "errors": []
}
```

Returns `401` if the `Authorization` header is missing or invalid, `503` if `ADMIN_KEY` is not configured.

### GET /health — Health Check

```bash
curl http://localhost:3000/health
```

Response: `{ "status": "ok" }`

## Database & Import

The SQLite database is stored at `data/products.sqlite` and is auto-created on first run.

To bulk-import products from a JSON file:

```bash
bun run scripts/import.ts products.json
```

Expected JSON format:
```json
[
  {
    "barcode": "8593894600019",
    "name": "Tatranky",
    "energy_kcal_100g": 520,
    "protein_100g": 7,
    "fat_100g": 28,
    "carbs_100g": 60,
    "serving_size": "47g",
    "image_url": null
  }
]
```

## Docker

```bash
# Build
docker build -t kalai-backend .

# Run
docker run -d \
  -p 3000:3000 \
  -e OPENAI_API_KEY=sk-... \
  -v kalai-data:/data \
  kalai-backend
```

The `-v kalai-data:/data` volume persists the SQLite database across container restarts.

## Project Structure

```
backend/
├── src/
│   ├── index.ts              # Server entry point, routing
│   ├── db/
│   │   ├── schema.ts         # Database init (WAL mode, schema)
│   │   └── products.ts       # Product queries & upserts
│   ├── middleware/
│   │   └── auth.ts           # Admin auth (Bearer token)
│   ├── routes/
│   │   ├── admin.ts          # POST /api/admin/import — bulk import
│   │   ├── analyze.ts        # POST /cal — OpenAI vision analysis
│   │   ├── barcode.ts        # GET /api/barcode/:code
│   │   └── search.ts         # GET /api/search?q=
│   └── services/
│       └── openai.ts         # OpenAI GPT-5-mini client
├── scripts/
│   └── import.ts             # Bulk JSON product import
├── data/                     # SQLite database directory
├── package.json
├── tsconfig.json
├── Dockerfile
├── .env.example
└── .gitignore
```

## Connecting from the Kotlin App

Update the `FoodAnalysisClient` base URL in the shared module to point to this backend:

```
http://<your-ip>:3000
```

The `POST /cal` response format matches the app's `FoodAnalysisDto` data class.
