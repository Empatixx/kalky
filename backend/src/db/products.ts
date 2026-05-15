import type { Product as PrismaProduct } from "@prisma/client";
import { prisma } from "./prisma";

export type Product = PrismaProduct;

export async function getProductByBarcode(barcode: string): Promise<Product | null> {
  return prisma.product.findUnique({ where: { barcode } });
}

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

    const products = await prisma.product.findMany({ where: { id: { in: ids } } });

    const order = new Map(ids.map((id, i) => [id, i]));
    return products.sort((a, b) => (order.get(a.id) ?? 0) - (order.get(b.id) ?? 0));
  }

  return prisma.product.findMany({
    where: { name: { contains: query } },
    take: limit,
  });
}

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

  if (input.barcode === null || input.barcode === "") {
    return prisma.product.create({ data });
  }

  return prisma.product.upsert({
    where: { barcode: input.barcode },
    create: { barcode: input.barcode, ...data },
    update: data,
  });
}
