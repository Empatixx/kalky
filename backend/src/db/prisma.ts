import { PrismaClient } from "@prisma/client";
import { mkdir } from "node:fs/promises";
import { dirname } from "node:path";

const DB_FILE = new URL("../../data/products.sqlite", import.meta.url).pathname;

if (!process.env.DATABASE_URL) {
  process.env.DATABASE_URL = `file:${DB_FILE}`;
}

export const prisma = new PrismaClient({
  log: ["error", "warn"],
});

export async function ensureDataDir(): Promise<void> {
  await mkdir(dirname(DB_FILE), { recursive: true });
}
