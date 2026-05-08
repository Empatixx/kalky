FROM oven/bun:1 AS base
WORKDIR /app

# Install deps + generate Prisma client (postinstall runs `prisma generate`).
COPY package.json bun.lock* ./
COPY prisma/ ./prisma/
RUN bun install --frozen-lockfile || bun install

COPY src/ ./src/
COPY scripts/ ./scripts/
COPY tsconfig.json ./

RUN mkdir -p data

EXPOSE 3000

# Apply pending migrations (idempotent) before starting the server. This
# replaces the old "CREATE IF NOT EXISTS + ALTER TABLE in try-catch" hack.
CMD ["sh", "-c", "bunx prisma migrate deploy && bun run src/index.ts"]
