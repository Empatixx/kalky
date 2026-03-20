#!/usr/bin/env python3
"""
Import scraped Kaufland products into the Kalai backend.

Usage:
    python3 import_to_backend.py                          # uses products.json
    python3 import_to_backend.py --file products.json
    python3 import_to_backend.py --host http://localhost:3000 --key YOUR_ADMIN_KEY
"""
import argparse
import json
import sys
from urllib.request import Request, urlopen
from urllib.error import HTTPError, URLError


def main():
    parser = argparse.ArgumentParser(description="Import scraped products to Kalai backend")
    parser.add_argument("--file", default="products.json", help="JSON file to import")
    parser.add_argument("--host", default="http://localhost:3000", help="Backend URL")
    parser.add_argument("--key", default="admin", help="Admin API key")
    parser.add_argument("--batch-size", type=int, default=50, help="Products per request")
    args = parser.parse_args()

    with open(args.file) as f:
        products = json.load(f)

    print(f"Loaded {len(products)} products from {args.file}")

    # Filter out products without names
    products = [p for p in products if p.get("name")]
    print(f"After filtering: {len(products)} products with names")

    total_imported = 0
    total_failed = 0

    for i in range(0, len(products), args.batch_size):
        batch = products[i : i + args.batch_size]
        payload = json.dumps({"products": batch}).encode("utf-8")

        req = Request(
            f"{args.host}/api/admin/import",
            data=payload,
            headers={
                "Content-Type": "application/json",
                "Authorization": f"Bearer {args.key}",
            },
            method="POST",
        )

        try:
            with urlopen(req) as resp:
                result = json.loads(resp.read())
                imported = result.get("imported", 0)
                failed = result.get("failed", 0)
                total_imported += imported
                total_failed += failed
                errors = result.get("errors", [])
                if errors:
                    for err in errors[:3]:
                        print(f"  Warning: {err}")
                print(f"  Batch {i // args.batch_size + 1}: imported={imported}, failed={failed}")
        except HTTPError as e:
            body = e.read().decode()
            print(f"  Batch {i // args.batch_size + 1} FAILED: {e.code} {body[:200]}")
            total_failed += len(batch)
        except URLError as e:
            print(f"  Connection error: {e.reason}")
            print("  Is the backend running?")
            sys.exit(1)

    print(f"\nDone! Imported: {total_imported}, Failed: {total_failed}")


if __name__ == "__main__":
    main()
