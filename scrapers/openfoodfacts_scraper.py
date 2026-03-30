#!/usr/bin/env python3
"""Import Czech food products from Open Food Facts CSV dump into Kalky backend."""

import argparse

from openfoodfacts import ProductDataset

from common import get_config, import_batch, map_off_product

CZECH_COUNTRY_TAGS = ("en:czech-republic", "en:czechia", "cs:česko", "cs:česká-republika")


def is_czech_product(product: dict) -> bool:
    """Check if a product is sold in Czech Republic."""
    countries_tags = product.get("countries_tags", [])
    if isinstance(countries_tags, list):
        return any(tag in CZECH_COUNTRY_TAGS for tag in countries_tags)
    if isinstance(countries_tags, str):
        return any(tag in countries_tags for tag in CZECH_COUNTRY_TAGS)
    return False


def main():
    parser = argparse.ArgumentParser(description="Import Czech products from Open Food Facts CSV dump")
    parser.add_argument("--limit", type=int, default=500, help="Max Czech products to import (default: 500)")
    parser.add_argument("--dry-run", action="store_true", help="Map products without importing")
    args = parser.parse_args()

    api_url, admin_key = "", ""
    if not args.dry_run:
        api_url, admin_key = get_config()

    print(f"Downloading Open Food Facts CSV dataset and filtering Czech products (limit: {args.limit})...")

    dataset = ProductDataset(dataset_type="csv")

    mapped = []
    scanned = 0
    skipped = 0

    for product in dataset:
        scanned += 1
        if scanned % 50000 == 0:
            print(f"  Scanned {scanned} products, found {len(mapped)} Czech...", flush=True)

        if not is_czech_product(product):
            continue

        result = map_off_product(product)
        if not result:
            skipped += 1
            continue
        # Skip products with no nutritional data
        if result["energy_kcal_100g"] == 0 and result["protein_100g"] == 0 and result["fat_100g"] == 0 and result["carbs_100g"] == 0:
            skipped += 1
            continue
        mapped.append(result)

        if len(mapped) >= args.limit:
            break

    print(f"\nScanned {scanned} total products")
    print(f"Found {len(mapped)} Czech products (skipped {skipped} without name)")

    total_imported = 0
    total_failed = 0

    if args.dry_run:
        print("Dry-run mode — not importing")
        for p in mapped[:5]:
            print(f"  Sample: {p['name']} ({p['energy_kcal_100g']} kcal)")
    elif mapped:
        print(f"Importing {len(mapped)} products...")
        total_imported, total_failed = import_batch(mapped, api_url, admin_key)

    print("\n--- Summary ---")
    print(f"Products scanned: {scanned}")
    print(f"Czech products mapped: {len(mapped)}")
    print(f"Products skipped (no name): {skipped}")
    if not args.dry_run:
        print(f"Products imported: {total_imported}")
        print(f"Products failed: {total_failed}")


if __name__ == "__main__":
    main()
