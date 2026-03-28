import { Database } from "bun:sqlite";
import { mkdir } from "node:fs/promises";
import { dirname } from "node:path";

const DB_PATH = new URL("../../data/products.sqlite", import.meta.url).pathname;

let db: Database;

export function getDb(): Database {
  if (!db) {
    throw new Error("Database not initialized. Call initDb() first.");
  }
  return db;
}

export async function initDb(): Promise<Database> {
  await mkdir(dirname(DB_PATH), { recursive: true });

  db = new Database(DB_PATH, { create: true });
  db.exec("PRAGMA journal_mode = WAL");
  db.exec("PRAGMA foreign_keys = ON");

  db.exec(`
    CREATE TABLE IF NOT EXISTS products (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      barcode TEXT UNIQUE,
      name TEXT NOT NULL,
      energy_kcal_100g REAL DEFAULT 0,
      protein_100g REAL DEFAULT 0,
      fat_100g REAL DEFAULT 0,
      carbs_100g REAL DEFAULT 0,
      serving_size TEXT,
      image_url TEXT,
      created_at TEXT DEFAULT (datetime('now')),
      updated_at TEXT DEFAULT (datetime('now'))
    );
  `);

  db.exec(`CREATE INDEX IF NOT EXISTS idx_products_barcode ON products(barcode)`);
  db.exec(`CREATE INDEX IF NOT EXISTS idx_products_name ON products(name)`);

  db.exec(`
    CREATE TABLE IF NOT EXISTS users (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      firebase_uid TEXT UNIQUE NOT NULL,
      email TEXT,
      display_name TEXT,
      photo_url TEXT,
      created_at TEXT DEFAULT (datetime('now')),
      updated_at TEXT DEFAULT (datetime('now'))
    );
  `);
  db.exec(`CREATE INDEX IF NOT EXISTS idx_users_firebase_uid ON users(firebase_uid)`);

  // FTS5 virtual table: indexes product name, strips Czech diacritics
  db.exec(`
    CREATE VIRTUAL TABLE IF NOT EXISTS products_fts USING fts5(
      name,
      content=products,
      content_rowid=id,
      tokenize='unicode61 remove_diacritics 2'
    );
  `);

  // Triggers to keep FTS in sync with products table
  db.exec(`
    CREATE TRIGGER IF NOT EXISTS products_ai AFTER INSERT ON products BEGIN
      INSERT INTO products_fts(rowid, name) VALUES (new.id, new.name);
    END;
  `);
  db.exec(`
    CREATE TRIGGER IF NOT EXISTS products_ad AFTER DELETE ON products BEGIN
      INSERT INTO products_fts(products_fts, rowid, name) VALUES('delete', old.id, old.name);
    END;
  `);
  db.exec(`
    CREATE TRIGGER IF NOT EXISTS products_au AFTER UPDATE ON products BEGIN
      INSERT INTO products_fts(products_fts, rowid, name) VALUES('delete', old.id, old.name);
      INSERT INTO products_fts(rowid, name) VALUES (new.id, new.name);
    END;
  `);

  // Rebuild FTS index from existing data (idempotent, safe on every startup)
  db.exec(`INSERT INTO products_fts(products_fts) VALUES('rebuild')`);

  return db;
}
