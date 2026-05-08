-- FTS5 virtual table for Czech product name search.
-- Tokenizer: unicode61 with diacritics stripped — "mle" matches "mléko".
CREATE VIRTUAL TABLE IF NOT EXISTS "products_fts" USING fts5(
    name,
    content="products",
    content_rowid="id",
    tokenize='unicode61 remove_diacritics 2'
);

-- Keep FTS index in sync with the products table.
CREATE TRIGGER IF NOT EXISTS "products_ai" AFTER INSERT ON "products" BEGIN
    INSERT INTO products_fts(rowid, name) VALUES (new.id, new.name);
END;

CREATE TRIGGER IF NOT EXISTS "products_ad" AFTER DELETE ON "products" BEGIN
    INSERT INTO products_fts(products_fts, rowid, name) VALUES('delete', old.id, old.name);
END;

CREATE TRIGGER IF NOT EXISTS "products_au" AFTER UPDATE ON "products" BEGIN
    INSERT INTO products_fts(products_fts, rowid, name) VALUES('delete', old.id, old.name);
    INSERT INTO products_fts(rowid, name) VALUES (new.id, new.name);
END;

-- Backfill the FTS index from any rows that already exist (idempotent on rebuild).
INSERT INTO products_fts(products_fts) VALUES('rebuild');
