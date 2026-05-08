# Kalky Backend

## Project Overview
Bun backend for the Kalky food tracking app. Provides food image analysis (via OpenAI GPT-5-mini vision), barcode product lookup, and text search. Persistence uses **Prisma ORM** on SQLite, with FTS5 for Czech text search.

## Architecture
- **Runtime**: Bun
- **Language**: TypeScript (strict mode)
- **ORM**: Prisma 5.x with SQLite provider
- **Database**: SQLite (Prisma manages schema + migrations)
- **Search**: FTS5 virtual table maintained by triggers; queried via `prisma.$queryRaw`
- **AI**: OpenAI SDK (`gpt-5-mini` vision model)
- **Server**: `Bun.serve` on `0.0.0.0:${PORT}`

## Project Structure
```
backend/
├── prisma/
│   ├── schema.prisma            # Product + User models, generator + datasource
│   └── migrations/
│       ├── 0_init/migration.sql # Initial tables + indexes
│       ├── 1_fts5/migration.sql # FTS5 virtual table + sync triggers
│       └── migration_lock.toml
├── src/
│   ├── index.ts                 # Server entry point + routing
│   ├── db/
│   │   ├── prisma.ts            # Prisma client singleton + ensureDataDir()
│   │   ├── products.ts          # getProductByBarcode, searchProducts (FTS5), upsertProduct
│   │   └── users.ts             # getOrCreateUser, getUserByFirebaseUid, updateFcmToken
│   ├── middleware/auth.ts       # Firebase Auth + AppCheck + ADMIN_KEY guards
│   ├── routes/
│   │   ├── admin.ts             # POST /api/admin/import — bulk product import (transactional)
│   │   ├── analyze.ts           # POST /cal — image analysis
│   │   ├── auth.ts              # POST /api/auth/me — register/lookup user
│   │   ├── barcode.ts           # GET /api/barcode/:code — lookup
│   │   ├── fcm.ts               # POST /api/auth/fcm-token — store push token
│   │   └── search.ts            # GET /api/search?q= — FTS5 prefix search
│   └── services/
│       ├── firebase.ts          # Firebase Admin token verification
│       └── openai.ts            # OpenAI vision client
├── scripts/
│   └── import.ts                # JSON product import script (uses Prisma)
├── data/
│   └── products.sqlite          # SQLite database file (path: file:../data/products.sqlite)
├── package.json
├── tsconfig.json
├── Dockerfile                   # Runs `prisma migrate deploy` before server start
└── .env                         # DATABASE_URL (optional override)
```

## Database Schema
Prisma models live in `prisma/schema.prisma`. Generated migrations live alongside under `prisma/migrations/`.

```prisma
model Product {
  id              Int      @id @default(autoincrement())
  barcode         String?  @unique
  name            String
  energyKcal100g  Float    @default(0) @map("energy_kcal_100g")
  protein100g     Float    @default(0) @map("protein_100g")
  fat100g         Float    @default(0) @map("fat_100g")
  carbs100g       Float    @default(0) @map("carbs_100g")
  servingSize     String?  @map("serving_size")
  imageUrl        String?  @map("image_url")
  createdAt       DateTime @default(now()) @map("created_at")
  updatedAt       DateTime @updatedAt @map("updated_at")

  @@index([barcode])
  @@index([name])
  @@map("products")
}

model User {
  id           Int      @id @default(autoincrement())
  firebaseUid  String   @unique @map("firebase_uid")
  email        String?
  displayName  String?  @map("display_name")
  photoUrl     String?  @map("photo_url")
  fcmToken     String?  @map("fcm_token")
  createdAt    DateTime @default(now()) @map("created_at")
  updatedAt    DateTime @updatedAt @map("updated_at")

  @@index([firebaseUid])
  @@map("users")
}
```

The DB column names stay snake_case (Kotlin/JSON consumers expect them); the Prisma TypeScript API uses camelCase via `@map`.

## FTS5 search
Prisma's SQLite provider doesn't model virtual tables, so the FTS5 layer lives in raw SQL inside `prisma/migrations/1_fts5/migration.sql`:

- `products_fts` virtual table, tokenizer `unicode61 remove_diacritics 2` ("mle" matches "mléko")
- Triggers `products_ai`, `products_au`, `products_ad` keep the index in sync after every INSERT/UPDATE/DELETE on `products`
- Initial backfill via `INSERT INTO products_fts(products_fts) VALUES('rebuild')`

Queries go through `prisma.$queryRaw` in `src/db/products.ts` — see `searchProducts()`. Falls back to a `LIKE` scan if FTS returns no rows.

## API Endpoints
Same surface as before; only the JSON shape changed slightly because Prisma returns `camelCase` field names:

### `POST /cal` — Food Image Analysis
Unchanged. Requires Firebase Auth + App Check.

### `GET /api/barcode/:code` — Product Lookup
Returns `Product | { error: "Product not found" }` (404).
Auth: Firebase + App Check.

### `GET /api/search?q=...` — FTS5 Search
Returns `Product[]`, max 20 results.
Auth: Firebase + App Check.

### `POST /api/auth/me` — Idempotent User Upsert
Body: empty. Auth header: Firebase ID token. Returns the User row.
Auth: Firebase + App Check.

### `POST /api/auth/fcm-token` — Store FCM token for the authenticated user
Body: `{ "token": "..." }`. Auth: Firebase + App Check.

### `POST /api/admin/import` — Bulk Product Upsert
Body: `{ "products": [...] }` or bare array. Each product field name is **snake_case** in the request body (preserved for compatibility with the Python scrapers), converted to camelCase before the Prisma write. Wrapped in a single `prisma.$transaction` for atomicity.
Auth: `Authorization: Bearer <ADMIN_KEY>`.

### `GET /health` — Liveness
Returns `{ "status": "ok" }`. No auth.

## Environment Variables
- `DATABASE_URL` — SQLite URL, e.g. `file:../data/products.sqlite`. If unset, `src/db/prisma.ts` derives a path under `data/`.
- `OPENAI_API_KEY` — Required for `/cal`
- `PORT` — Server port (default 3000)
- `ADMIN_KEY` — Required for `/api/admin/import`
- `GOOGLE_APPLICATION_CREDENTIALS` — Firebase service account JSON for token verification

## Build & Run
```bash
bun install                                   # Installs deps + runs `prisma generate`
bunx prisma migrate dev --name <change>       # Local schema change → new migration
bunx prisma migrate deploy                    # Apply pending migrations (production)
bun run src/index.ts                          # Production
bun --watch run src/index.ts                  # Dev with hot reload (or: bun run dev)
```

The Docker image runs `bunx prisma migrate deploy` before starting the server, so schema is always up-to-date in production.

## Adding a schema change
1. Edit `prisma/schema.prisma`.
2. Run `bunx prisma migrate dev --name short_description` — Prisma generates an SQL migration in `prisma/migrations/<timestamp>_short_description/`.
3. Commit both the schema and the migration folder.
4. The next `prisma migrate deploy` (in CI/Dockerfile) applies it idempotently.

For FTS5 / virtual-table changes, edit `prisma/migrations/1_fts5/migration.sql` directly or add a new migration with raw SQL — Prisma's schema language doesn't model these.

## Import Script
```bash
bun run scripts/import.ts <path-to-json>
```
Same JSON format as before (snake_case keys). Uses `prisma.product.upsert()` per row.

## CI/CD
- GitHub Actions workflow: `.github/workflows/backend.yml`
- Steps: `bun install` (runs `prisma generate`) → `bun tsc --noEmit` → `docker build` → deploy

## Key Conventions
- CORS enabled on all routes (`Access-Control-Allow-Origin: *`)
- OpenAI client initialized lazily on the first `/cal` request
- Prisma client is a process-wide singleton (`src/db/prisma.ts`) — do not instantiate it elsewhere
- All DB-touching functions return Promises — handlers must `await` them
- Snake_case is preserved at the request/response boundary for `/api/admin/import` (scraper compat); everything internal uses camelCase
