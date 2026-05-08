import type { Product as PrismaProduct } from "@prisma/client";
import { prisma } from "./prisma";

/** Public product shape returned by the API (camelCase, JSON-friendly). */
export type Product = PrismaProduct;

export async function getProductByBarcode(barcode: string): Promise<Product | null> {
  return prisma.product.findUnique({ where: { barcode } });
}

/**
 * FTS5-backed product name search with prefix matching. Falls back to a LIKE
 * scan if FTS returns nothing (handles single-character or symbol-only queries).
 *
 * Prisma's SQLite provider doesn't model virtual tables, so the FTS5 join goes
 * through `$queryRaw`. Tokens are split on whitespace and each gets a `*`
 * suffix so "mle" matches "mléko".
 */
export async function searchProducts(query: string, limit = 20): Promise<Product[]> {
  const tokens = query.trim().split(/\s+/).filter(Boolean);
  if (tokens.length === 0) return [];

  const ftsQuery = tokens.map((t) => `"${t.replace(/"/g, "")}"*`).join(" ");

  const ftsRows = await prisma.$queryRaw<Array<{ id: number }>>`
    SELECT p.id AS id
    FROM products p
    JOIN products_fts fts ON fts.rowid = p.id
    WHERE products_fts MATCH ${ftsQuery}
    ORDER BY fts.rank
    LIMIT ${limit}
  `;

  if (ftsRows.length > 0) {
    const ids = ftsRows.map((r) => r.id);
    // Re-fetch via Prisma to keep camelCase mapping + relations consistent.
    const products = await prisma.product.findMany({ where: { id: { in: ids } } });
    // Preserve FTS5 rank order (findMany doesn't guarantee it).
    const order = new Map(ids.map((id, i) => [id, i]));
    return products.sort((a, b) => (order.get(a.id) ?? 0) - (order.get(b.id) ?? 0));
  }

  return prisma.product.findMany({
    where: { name: { contains: query } },
    take: limit,
  });
}

/** Camel-cased input shape for upsert. Routes/scripts convert from snake_case. */
export interface ProductUpsertInput {
  barcode: string | null;
  name: string;
  energyKcal100g: number;
  protein100g: number;
  fat100g: number;
  carbs100g: number;
  servingSize: string | null;
  imageUrl: string | null;
}

export async function upsertProduct(input: ProductUpsertInput): Promise<Product> {
  const data = {
    name: input.name,
    energyKcal100g: input.energyKcal100g,
    protein100g: input.protein100g,
    fat100g: input.fat100g,
    carbs100g: input.carbs100g,
    servingSize: input.servingSize,
    imageUrl: input.imageUrl,
  };

  // Without a barcode there's no upsert key — always insert.
  if (input.barcode === null || input.barcode === "") {
    return prisma.product.create({ data });
  }

  return prisma.product.upsert({
    where: { barcode: input.barcode },
    create: { barcode: input.barcode, ...data },
    update: data,
  });
}
