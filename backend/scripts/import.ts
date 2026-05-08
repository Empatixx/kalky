import { ensureDataDir, prisma } from "../src/db/prisma";
import { upsertProduct } from "../src/db/products";

/**
 * Product data import script.
 *
 * Usage: bun run scripts/import.ts <path-to-json>
 *
 * Expected JSON format: array of objects with snake_case fields:
 *   barcode, name, energy_kcal_100g, protein_100g, fat_100g, carbs_100g,
 *   serving_size (optional), image_url (optional).
 */

const filePath = process.argv[2];
if (!filePath) {
  console.log("Usage: bun run scripts/import.ts <path-to-json>");
  console.log("  JSON file should contain an array of product objects.");
  process.exit(1);
}

await ensureDataDir();
await prisma.$connect();

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
    await upsertProduct({
      barcode: p.barcode ?? null,
      name: p.name,
      energyKcal100g: Number(p.energy_kcal_100g) || 0,
      protein100g: Number(p.protein_100g) || 0,
      fat100g: Number(p.fat_100g) || 0,
      carbs100g: Number(p.carbs_100g) || 0,
      servingSize: p.serving_size ?? null,
      imageUrl: p.image_url ?? null,
    });
    imported++;
  } catch (err) {
    console.error(`Failed to import product "${p.name}":`, err);
  }
}

console.log(`Imported ${imported}/${products.length} products.`);
await prisma.$disconnect();
