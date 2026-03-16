import { getDb } from "./schema";

export interface Product {
  id: number;
  barcode: string | null;
  name: string;
  energy_kcal_100g: number;
  protein_100g: number;
  fat_100g: number;
  carbs_100g: number;
  serving_size: string | null;
  image_url: string | null;
  created_at: string;
  updated_at: string;
}

export function getProductByBarcode(barcode: string): Product | null {
  const db = getDb();
  return db.query<Product, [string]>(
    "SELECT * FROM products WHERE barcode = ?"
  ).get(barcode);
}

export function searchProducts(query: string, limit: number = 20): Product[] {
  const db = getDb();

  // FTS5 search with relevance ranking (bm25)
  // Append * for prefix matching: "mle" matches "mléko", "mlekárna", etc.
  const ftsQuery = query.split(/\s+/).map(t => `"${t}"*`).join(' ');
  const results = db.query<Product, [string, number]>(
    `SELECT p.* FROM products p
     JOIN products_fts fts ON fts.rowid = p.id
     WHERE products_fts MATCH ?
     ORDER BY fts.rank
     LIMIT ?`
  ).all(ftsQuery, limit);

  // Fallback to LIKE if FTS returns nothing (handles edge cases)
  if (results.length === 0) {
    return db.query<Product, [string, number]>(
      "SELECT * FROM products WHERE name LIKE ? LIMIT ?"
    ).all(`%${query}%`, limit);
  }

  return results;
}

export function insertProduct(product: Omit<Product, "id" | "created_at" | "updated_at">): Product | null {
  const db = getDb();
  const result = db.query<Product, Record<string, unknown>>(`
    INSERT INTO products (barcode, name, energy_kcal_100g, protein_100g, fat_100g, carbs_100g, serving_size, image_url)
    VALUES ($barcode, $name, $energy_kcal_100g, $protein_100g, $fat_100g, $carbs_100g, $serving_size, $image_url)
    ON CONFLICT(barcode) DO UPDATE SET
      name = excluded.name,
      energy_kcal_100g = excluded.energy_kcal_100g,
      protein_100g = excluded.protein_100g,
      fat_100g = excluded.fat_100g,
      carbs_100g = excluded.carbs_100g,
      serving_size = excluded.serving_size,
      image_url = excluded.image_url,
      updated_at = datetime('now')
    RETURNING *
  `).get({
    $barcode: product.barcode,
    $name: product.name,
    $energy_kcal_100g: product.energy_kcal_100g,
    $protein_100g: product.protein_100g,
    $fat_100g: product.fat_100g,
    $carbs_100g: product.carbs_100g,
    $serving_size: product.serving_size,
    $image_url: product.image_url,
  });
  return result;
}
