import { initDb } from "../src/db/schema";
import { insertProduct } from "../src/db/products";

/**
 * Product data import script.
 *
 * Usage: bun run scripts/import.ts <path-to-json-or-csv>
 *
 * Expected JSON format: array of objects with fields:
 *   barcode, name, energy_kcal_100g, protein_100g, fat_100g, carbs_100g,
 *   serving_size (optional), image_url (optional)
 *
 * Extend this script to add CSV parsing or scraper integration as needed.
 */

const filePath = process.argv[2];
if (!filePath) {
  console.log("Usage: bun run scripts/import.ts <path-to-json>");
  console.log("  JSON file should contain an array of product objects.");
  process.exit(1);
}

await initDb();

const file = Bun.file(filePath);
const text = await file.text();
const products = JSON.parse(text);

if (!Array.isArray(products)) {
  console.error("Expected a JSON array of products.");
  process.exit(1);
}

let imported = 0;
for (const p of products) {
  try {
    insertProduct({
      barcode: p.barcode ?? null,
      name: p.name,
      energy_kcal_100g: p.energy_kcal_100g ?? 0,
      protein_100g: p.protein_100g ?? 0,
      fat_100g: p.fat_100g ?? 0,
      carbs_100g: p.carbs_100g ?? 0,
      serving_size: p.serving_size ?? null,
      image_url: p.image_url ?? null,
    });
    imported++;
  } catch (err) {
    console.error(`Failed to import product "${p.name}":`, err);
  }
}

console.log(`Imported ${imported}/${products.length} products.`);
