FROM oven/bun:1 AS base
WORKDIR /app

COPY package.json bun.lock* ./
RUN bun install --frozen-lockfile || bun install

COPY src/ ./src/
COPY scripts/ ./scripts/
COPY tsconfig.json ./

RUN mkdir -p data

EXPOSE 3000

CMD ["bun", "run", "src/index.ts"]
