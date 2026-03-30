# Kalky Product Scrapers

Standalone Python scrapers that collect Czech food product data and import it into the Kalky backend via the admin API.

## Setup

```bash
cd scrapers
pip install -r requirements.txt

# Configure environment
cp .env.example .env
# Edit .env and set ADMIN_KEY to match the backend's ADMIN_KEY
```

## Scrapers

### Open Food Facts Scraper

Fetches Czech food products from the [Open Food Facts](https://world.openfoodfacts.org/) database.

```bash
# Dry run — fetch and display without importing
python openfoodfacts_scraper.py --pages 2 --dry-run

# Import 5 pages starting from page 1
python openfoodfacts_scraper.py --pages 5

# Import pages 10-20
python openfoodfacts_scraper.py --start-page 10 --pages 10
```

**Options:**
- `--pages N` — Number of pages to scrape (default: 50, ~20 products per page)
- `--start-page N` — Starting page number (default: 1)
- `--dry-run` — Fetch and map products without sending to the backend

**Data source:** [Open Food Facts API](https://wiki.openfoodfacts.org/API) — Czech Republic products. Includes barcode, name, nutritional values per 100g, serving size, and product image URL.

### Czech Kalorické Tabulky Scraper

Scrapes Czech food nutritional data from kalorické tabulky (calorie tables) websites.

```bash
python kaloricke_tabulky_scraper.py
```

**Notes:**
- Products from this source have no barcode — synthetic barcodes (`KT:{md5}`) are generated for deduplication
- Uses a 2-second delay between page fetches (polite scraping)

**Data source:** [kaloricketabulky.cz](https://www.kaloricketabulky.cz/) — Czech food database with nutritional values per 100g.

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `API_URL` | `http://localhost:3000` | Kalky backend URL |
| `ADMIN_KEY` | (required) | Admin API authentication key |
