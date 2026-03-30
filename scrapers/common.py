"""Shared helpers for Kalky product scrapers."""

import os
import requests


def get_config() -> tuple[str, str]:
    """Return (api_url, admin_key) from environment variables."""
    api_url = os.environ.get("API_URL", "http://localhost:3000").rstrip("/")
    admin_key = os.environ.get("ADMIN_KEY", "")
    if not admin_key:
        raise RuntimeError("ADMIN_KEY environment variable is required")
    return api_url, admin_key


def import_batch(
    products: list[dict],
    api_url: str,
    admin_key: str,
    batch_size: int = 100,
) -> tuple[int, int]:
    """POST products in batches to /api/admin/import. Returns (imported, failed)."""
    total_imported = 0
    total_failed = 0

    for i in range(0, len(products), batch_size):
        batch = products[i : i + batch_size]
        resp = requests.post(
            f"{api_url}/api/admin/import",
            json={"products": batch},
            headers={
                "Authorization": f"Bearer {admin_key}",
                "Content-Type": "application/json",
            },
            timeout=60,
        )
        if resp.status_code != 200:
            print(f"  Batch {i // batch_size + 1} failed: {resp.status_code} {resp.text}")
            total_failed += len(batch)
            continue

        data = resp.json()
        total_imported += data.get("imported", 0)
        total_failed += data.get("failed", 0)
        if data.get("errors"):
            for err in data["errors"][:5]:
                print(f"  Warning: {err}")

    return total_imported, total_failed


def _float(val) -> float:
    """Safely convert a value to float, returning 0 on failure."""
    if not val:
        return 0.0
    try:
        return float(val)
    except (ValueError, TypeError):
        return 0.0


def map_off_product(off_product: dict) -> dict | None:
    """Map an Open Food Facts product dict to the backend product shape.

    Handles both JSON API format (nested nutriments) and CSV dump format (flat keys).
    Returns None if the product lacks required fields.
    """
    code = str(off_product.get("code", "")).strip()
    name = str(off_product.get("product_name", "")).strip()

    if not name:
        return None

    # JSON API: nutrients nested under "nutriments"
    nutriments = off_product.get("nutriments", {})
    if nutriments:
        energy = _float(nutriments.get("energy-kcal_100g"))
        protein = _float(nutriments.get("proteins_100g"))
        fat = _float(nutriments.get("fat_100g"))
        carbs = _float(nutriments.get("carbohydrates_100g"))
    else:
        # CSV dump: flat keys directly on product
        energy = _float(off_product.get("energy-kcal_100g"))
        protein = _float(off_product.get("proteins_100g"))
        fat = _float(off_product.get("fat_100g"))
        carbs = _float(off_product.get("carbohydrates_100g"))

    return {
        "barcode": code or None,
        "name": name,
        "energy_kcal_100g": energy,
        "protein_100g": protein,
        "fat_100g": fat,
        "carbs_100g": carbs,
        "serving_size": off_product.get("serving_size") or None,
        "image_url": off_product.get("image_url") or None,
    }
