import { upsertProduct } from "../db/products";
import { prisma } from "../db/prisma";

interface ImportProduct {
  barcode?: string | null;
  name?: string;
  energy_kcal_100g?: number;
  protein_100g?: number;
  fat_100g?: number;
  carbs_100g?: number;
  serving_size?: string | null;
  image_url?: string | null;
}

export async function handleAdminImport(req: Request): Promise<Response> {
  let body: unknown;
  try {
    body = await req.json();
  } catch {
    return Response.json({ error: "Invalid JSON body" }, { status: 400 });
  }

  let products: ImportProduct[];
  if (Array.isArray(body)) {
    products = body;
  } else if (
    body &&
    typeof body === "object" &&
    "products" in body &&
    Array.isArray((body as { products: unknown }).products)
  ) {
    products = (body as { products: ImportProduct[] }).products;
  } else {
    return Response.json(
      { error: "Expected { products: [...] } or array [...]" },
      { status: 400 },
    );
  }

  if (products.length === 0) {
    return Response.json({ imported: 0, failed: 0, errors: [] });
  }

  const errors: string[] = [];
  let imported = 0;
  let failed = 0;

  try {
    await prisma.$transaction(async (tx) => {
      for (let i = 0; i < products.length; i++) {
        const p = products[i];

        if (!p.name || typeof p.name !== "string" || p.name.trim() === "") {
          errors.push(`[${i}] Missing or empty 'name'`);
          failed++;
          continue;
        }

        try {

          const data = {
            name: p.name.trim(),
            energyKcal100g: Number(p.energy_kcal_100g) || 0,
            protein100g: Number(p.protein_100g) || 0,
            fat100g: Number(p.fat_100g) || 0,
            carbs100g: Number(p.carbs_100g) || 0,
            servingSize: p.serving_size ?? null,
            imageUrl: p.image_url ?? null,
          };
          const barcode = p.barcode ?? null;
          if (barcode === null || barcode === "") {
            await tx.product.create({ data });
          } else {
            await tx.product.upsert({
              where: { barcode },
              create: { barcode, ...data },
              update: data,
            });
          }
          imported++;
        } catch (err) {
          errors.push(`[${i}] ${err instanceof Error ? err.message : String(err)}`);
          failed++;
        }
      }
    });
  } catch (err) {
    return Response.json(
      {
        error: "Transaction failed",
        detail: err instanceof Error ? err.message : String(err),
      },
      { status: 500 },
    );
  }

  return Response.json({ imported, failed, errors });
}
