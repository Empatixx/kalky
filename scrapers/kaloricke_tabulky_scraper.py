#!/usr/bin/env python3
"""Scrape Czech food data from kaloricketabulky.cz and import into Kalky backend.

Crawls paginated food listing to discover food URLs, then fetches detail pages
for JSON-LD nutritional data.
"""

import argparse
import hashlib
import json
import re
import time

import requests
from bs4 import BeautifulSoup

from common import get_config, import_batch

BASE_URL = "https://www.kaloricketabulky.cz"
LISTING_URL = f"{BASE_URL}/tabulka-potravin"
HEADERS = {"User-Agent": "KalkyScraper/1.0"}


def generate_synthetic_barcode(name: str) -> str:
    """Generate a deterministic synthetic barcode from the food name."""
    md5 = hashlib.md5(name.encode("utf-8")).hexdigest()
    return f"KT:{md5}"


def parse_nutrient_from_keyword(keyword: str) -> tuple[str, float] | None:
    """Parse a nutrient name and value from a JSON-LD keyword string."""
    mapping = {
        "Energetická hodnota": "energy",
        "Bílkoviny": "protein",
        "Tuky": "fat",
        "Sacharidy": "carbs",
    }
    for label, key in mapping.items():
        if keyword.startswith(label):
            match = re.search(r":\s*([\d,]+)", keyword)
            if match:
                try:
                    return key, float(match.group(1).replace(",", "."))
                except ValueError:
                    pass
    return None


def get_food_slugs(page: int) -> list[str]:
    """Scrape food URL slugs from a listing page."""
    resp = requests.get(LISTING_URL, params={"page": page}, headers=HEADERS, timeout=30)
    resp.raise_for_status()

    slugs = re.findall(r'href="/potraviny/([a-z0-9][a-z0-9-]*)"', resp.text)
    # Deduplicate while preserving order
    seen = set()
    unique = []
    for s in slugs:
        if s not in seen:
            seen.add(s)
            unique.append(s)
    return unique


def fetch_food_detail(slug: str) -> dict | None:
    """Fetch a food detail page and extract nutrients from JSON-LD."""
    resp = requests.get(f"{BASE_URL}/potraviny/{slug}", headers=HEADERS, timeout=30)
    if resp.status_code != 200:
        return None

    soup = BeautifulSoup(resp.text, "html.parser")
    for script in soup.find_all("script", type="application/ld+json"):
        try:
            data = json.loads(script.string)
        except (json.JSONDecodeError, TypeError):
            continue
        if data.get("@type") != "Dataset":
            continue

        name = data.get("name", "").strip()
        if not name:
            continue

        nutrients = {"energy": 0.0, "protein": 0.0, "fat": 0.0, "carbs": 0.0}
        for kw in data.get("keywords", []):
            result = parse_nutrient_from_keyword(kw)
            if result:
                nutrients[result[0]] = result[1]

        return {
            "barcode": generate_synthetic_barcode(name),
            "name": name,
            "energy_kcal_100g": nutrients["energy"],
            "protein_100g": nutrients["protein"],
            "fat_100g": nutrients["fat"],
            "carbs_100g": nutrients["carbs"],
            "serving_size": None,
            "image_url": None,
        }
    return None


def main():
    parser = argparse.ArgumentParser(description="Import Czech foods from kaloricketabulky.cz")
    parser.add_argument("--pages", type=int, default=50, help="Number of listing pages to scrape (default: 50, 10 foods/page)")
    parser.add_argument("--dry-run", action="store_true", help="Fetch and show products without importing")
    args = parser.parse_args()

    api_url, admin_key = "", ""
    if not args.dry_run:
        api_url, admin_key = get_config()

    print(f"Scraping kaloricketabulky.cz: {args.pages} pages (~{args.pages * 10} foods)...")

    all_slugs = []
    for page in range(1, args.pages + 1):
        try:
            slugs = get_food_slugs(page)
            all_slugs.extend(slugs)
            if page % 10 == 0:
                print(f"  Listing page {page}/{args.pages}: {len(all_slugs)} food URLs collected", flush=True)
        except requests.exceptions.RequestException as e:
            print(f"  Page {page} error: {e}")
        time.sleep(0.5)

    print(f"\nCollected {len(all_slugs)} food URLs. Fetching detail pages...")

    products = []
    skipped = 0

    for i, slug in enumerate(all_slugs):
        if (i + 1) % 20 == 0:
            print(f"  Detail {i + 1}/{len(all_slugs)}: {len(products)} products...", flush=True)

        try:
            product = fetch_food_detail(slug)
            if product:
                products.append(product)
            else:
                skipped += 1
        except requests.exceptions.RequestException:
            skipped += 1

        time.sleep(1)  # polite scraping

    print(f"\nFetched {len(products)} products (skipped {skipped})")

    if args.dry_run:
        print("Dry-run mode — not importing")
        for p in products[:5]:
            print(f"  {p['name']}: {p['energy_kcal_100g']} kcal, {p['protein_100g']}g P, {p['fat_100g']}g F, {p['carbs_100g']}g C")
    elif products:
        print(f"Importing {len(products)} products...")
        imported, failed = import_batch(products, api_url, admin_key)
        print(f"\n--- Summary ---")
        print(f"Pages scraped: {args.pages}")
        print(f"Products imported: {imported}")
        print(f"Products failed: {failed}")
    else:
        print("No products to import")


if __name__ == "__main__":
    main()
