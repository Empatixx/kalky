import { PrismaClient } from "@prisma/client";
import { mkdir } from "node:fs/promises";
import { dirname } from "node:path";

const DB_FILE = new URL("../../data/products.sqlite", import.meta.url).pathname;

// Prisma reads DATABASE_URL at client construction. Allow overriding via env
// (e.g. docker compose), fall back to the local data/ path the app already uses.
if (!process.env.DATABASE_URL) {
  process.env.DATABASE_URL = `file:${DB_FILE}`;
}

export const prisma = new PrismaClient({
  log: ["error", "warn"],
});

/** Ensure the data directory exists before Prisma opens the SQLite file. */
export async function ensureDataDir(): Promise<void> {
  await mkdir(dirname(DB_FILE), { recursive: true });
}
