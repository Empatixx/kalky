import { getDb } from "../db/schema";
import { insertProduct } from "../db/products";

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
    return Response.json(
      { error: "Invalid JSON body" },
      { status: 400 }
    );
  }

  // Accept { products: [...] } or bare array [...]
  let products: ImportProduct[];
  if (Array.isArray(body)) {
    products = body;
  } else if (body && typeof body === "object" && "products" in body && Array.isArray((body as { products: unknown }).products)) {
    products = (body as { products: ImportProduct[] }).products;
  } else {
    return Response.json(
      { error: "Expected { products: [...] } or array [...]" },
      { status: 400 }
    );
  }

  if (products.length === 0) {
    return Response.json({ imported: 0, failed: 0, errors: [] });
  }

  const db = getDb();
  const errors: string[] = [];
  let imported = 0;
  let failed = 0;

  db.exec("BEGIN");
  try {
    for (let i = 0; i < products.length; i++) {
      const p = products[i];

      if (!p.name || typeof p.name !== "string" || p.name.trim() === "") {
        errors.push(`[${i}] Missing or empty 'name'`);
        failed++;
        continue;
      }

      try {
        insertProduct({
          barcode: p.barcode ?? null,
          name: p.name.trim(),
          energy_kcal_100g: Number(p.energy_kcal_100g) || 0,
          protein_100g: Number(p.protein_100g) || 0,
          fat_100g: Number(p.fat_100g) || 0,
          carbs_100g: Number(p.carbs_100g) || 0,
          serving_size: p.serving_size ?? null,
          image_url: p.image_url ?? null,
        });
        imported++;
      } catch (err) {
        errors.push(`[${i}] ${err instanceof Error ? err.message : String(err)}`);
        failed++;
      }
    }
    db.exec("COMMIT");
  } catch (err) {
    db.exec("ROLLBACK");
    return Response.json(
      { error: "Transaction failed", detail: err instanceof Error ? err.message : String(err) },
      { status: 500 }
    );
  }

  return Response.json({ imported, failed, errors });
}
