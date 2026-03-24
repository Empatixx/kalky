#!/usr/bin/env python3
"""
Tesco CZ scraper - stahuje potraviny s makronutrienty z nakup.itesco.cz
Používá GraphQL API (xapi.tesco.com)
"""

import requests
import json
import csv
import re
import time
import uuid
import sys
import random
from pathlib import Path

# Flush prints immediately
import functools
print = functools.partial(print, flush=True)


API_URL = "https://xapi.tesco.com/"
API_KEY = "TvOSZJHlEk0pjniDGQFAc9Q59WGAR4dA"
PRODUCTS_PER_PAGE = 24
OUTPUT_FILE = "tesco_potraviny.csv"


def make_headers():
    trace = str(uuid.uuid4())
    return {
        "Content-Type": "application/json",
        "Accept": "application/json",
        "x-apikey": API_KEY,
        "Origin": "https://nakup.itesco.cz",
        "Referer": "https://nakup.itesco.cz/",
        "language": "cs-CZ",
        "region": "CZ",
        "traceid": f"{trace}:{uuid.uuid4()}",
        "trkid": trace,
    }


SEARCH_QUERY = """query Search($query: String!, $page: Int = 1, $count: Int) {
    search(query: $query, page: $page, count: $count) {
        pageInformation: info {
            total
            pageSize
        }
        results {
            node {
                ... on ProductType {
                    tpnb
                    tpnc
                    gtin
                    title
                    brandName
                    price {
                        actual
                        unitPrice
                        unitOfMeasure
                    }
                }
                ... on MPProduct {
                    tpnb
                    tpnc
                    gtin
                    title
                    brandName
                    price {
                        actual
                        unitPrice
                        unitOfMeasure
                    }
                }
            }
        }
    }
}"""

CATEGORY_QUERY = """query Category($categoryId: ID!, $page: Int = 1, $count: Int) {
    category(categoryId: $categoryId, page: $page, count: $count) {
        pageInformation: info {
            total
            pageSize
        }
        results {
            node {
                ... on ProductType {
                    tpnb
                    tpnc
                    gtin
                    title
                    brandName
                    price {
                        actual
                        unitPrice
                        unitOfMeasure
                    }
                }
            }
        }
    }
}"""

TAXONOMY_QUERY = """query Taxonomy {
    taxonomy {
        name
        label
        children {
            name
            label
            id
            children {
                id
                name
                label
            }
        }
    }
}"""

DETAIL_QUERY = """query P($tpnc: String) {
    product(tpnc: $tpnc) {
        tpnb
        tpnc
        title
        brandName
        details {
            nutrition {
                name
                value1
                value2
            }
            guidelineDailyAmount {
                ... on GuidelineDailyAmountType {
                    title
                    dailyAmounts {
                        name
                        value
                        percent
                        rating
                    }
                }
            }
        }
    }
}"""


def api_call(payload, retries=5):
    for attempt in range(retries):
        try:
            r = requests.post(API_URL, headers=make_headers(), json=payload, timeout=20)
            if r.status_code in (502, 503, 429):
                raise requests.exceptions.ConnectionError(f"HTTP {r.status_code}")
            r.raise_for_status()
            return r.json()[0]
        except (requests.exceptions.Timeout, requests.exceptions.ConnectionError,
                requests.exceptions.HTTPError) as e:
            wait = 5 * (attempt + 1) + random.uniform(0, 3)
            print(f"    [retry {attempt+1}/{retries}, čekám {wait:.0f}s...]")
            time.sleep(wait)
    return {}


def search_products(query: str, page: int = 1, count: int = PRODUCTS_PER_PAGE) -> dict:
    payload = [{
        "operationName": "Search",
        "variables": {"query": query, "page": page, "count": count},
        "extensions": {"mfeName": "mfe-plp"},
        "query": SEARCH_QUERY,
    }]
    return api_call(payload).get("data", {}).get("search", {})


def get_taxonomy() -> list:
    payload = [{
        "operationName": "Taxonomy",
        "variables": {},
        "extensions": {"mfeName": "mfe-header"},
        "query": TAXONOMY_QUERY,
    }]
    return api_call(payload).get("data", {}).get("taxonomy", [])


def browse_category(cat_id: str, page: int = 1, count: int = PRODUCTS_PER_PAGE) -> dict:
    payload = [{
        "operationName": "Category",
        "variables": {"categoryId": cat_id, "page": page, "count": count},
        "extensions": {"mfeName": "mfe-plp"},
        "query": CATEGORY_QUERY,
    }]
    return api_call(payload).get("data", {}).get("category", {})


def get_product_nutrition(tpnc: str) -> dict:
    payload = [{
        "operationName": "P",
        "variables": {"tpnc": tpnc},
        "query": DETAIL_QUERY,
    }]
    resp = api_call(payload)
    if "errors" in resp:
        return {}
    return resp.get("data", {}).get("product", {})


def parse_nutrition(nutrition_list: list) -> dict:
    """Parse nutrition items into structured macronutrient data."""
    macros = {
        "energie_kj": None,
        "energie_kcal": None,
        "tuky_g": None,
        "nasycene_mk_g": None,
        "sacharidy_g": None,
        "cukry_g": None,
        "bilkoviny_g": None,
        "sul_g": None,
        "vlaknina_g": None,
    }

    prev_name = ""
    for item in nutrition_list:
        name = (item.get("name") or "").strip()
        name_lower = name.lower()
        val = (item.get("value1") or "").strip()

        if not val or name_lower in ("typical values",):
            continue

        # Handle "-" rows that continue the previous item (e.g. kcal after kJ)
        if name == "-" and prev_name:
            name_lower = prev_name
        else:
            prev_name = name_lower

        # Try kJ/kcal extraction from value
        kj_match = re.search(r"([\d,.]+)\s*kJ", val, re.I)
        kcal_match = re.search(r"([\d,.]+)\s*kcal", val, re.I)

        if "energie" in name_lower or "energet" in name_lower or "energy" in name_lower:
            if kj_match:
                macros["energie_kj"] = parse_num(kj_match.group(1))
            elif "kj" not in val.lower() and "kcal" not in val.lower():
                n = extract_number(val)
                if n and not macros["energie_kj"]:
                    macros["energie_kj"] = n
            if kcal_match:
                macros["energie_kcal"] = parse_num(kcal_match.group(1))
            elif "kcal" in val.lower():
                n = extract_number(val)
                if n:
                    macros["energie_kcal"] = n
            continue

        num = extract_number(val)
        if num is None:
            continue

        if name_lower in ("tuky", "tuk", "fat", "fats"):
            macros["tuky_g"] = num
        elif "nasycen" in name_lower or "saturated" in name_lower:
            macros["nasycene_mk_g"] = num
        elif name_lower in ("sacharidy", "uhlohydráty", "carbohydrate", "carbohydrates"):
            macros["sacharidy_g"] = num
        elif "cukr" in name_lower or "sugar" in name_lower:
            macros["cukry_g"] = num
        elif "bílkovin" in name_lower or "protein" in name_lower:
            macros["bilkoviny_g"] = num
        elif name_lower in ("sůl", "sul", "salt"):
            macros["sul_g"] = num
        elif "vlákn" in name_lower or "fibre" in name_lower or "fiber" in name_lower:
            macros["vlaknina_g"] = num

    return macros


def extract_number(text: str):
    """Extract first numeric value from text like '1,0 g' or '270 kJ'."""
    m = re.search(r"([\d,.]+)", text)
    if m:
        return parse_num(m.group(1))
    return None


def parse_num(s: str) -> float:
    """Parse Czech number format (comma as decimal)."""
    s = s.replace(" ", "").replace(",", ".")
    try:
        return float(s)
    except ValueError:
        return None


def scrape(search_terms: list[str], max_pages: int = 5, save_fn=None):
    """Main scraper - search for products and get their nutrition data."""
    all_products = []
    seen_tpnc = set()

    for term in search_terms:
        print(f"\n{'='*60}")
        print(f"Hledání: {term}")
        print(f"{'='*60}")

        page = 1
        while page <= max_pages:
            search_data = search_products(term, page=page)
            results = search_data.get("results", [])
            page_info = search_data.get("pageInformation", {})
            total = page_info.get("total", 0)
            page_size = page_info.get("pageSize", PRODUCTS_PER_PAGE)
            page_count = (total + page_size - 1) // page_size if page_size else 1

            if not results:
                break

            if page == 1:
                print(f"  Nalezeno: {total} produktů ({page_count} stránek)")

            for item in results:
                node = item.get("node", {})
                tpnc = node.get("tpnc")
                if not tpnc or tpnc in seen_tpnc:
                    continue
                seen_tpnc.add(tpnc)

                title = node.get("title", "")
                brand = node.get("brandName", "")
                gtin = node.get("gtin", "")
                price_data = node.get("price", {})
                price = price_data.get("actual")
                unit_price = price_data.get("unitPrice")
                unit = price_data.get("unitOfMeasure", "")

                # Get nutrition
                detail = get_product_nutrition(tpnc)
                details = detail.get("details", {})
                nutrition_list = details.get("nutrition", [])
                macros = parse_nutrition(nutrition_list)

                product = {
                    "tpnc": tpnc,
                    "ean": gtin,
                    "nazev": title,
                    "znacka": brand,
                    "cena_kc": price,
                    "jednotkova_cena": unit_price,
                    "jednotka": unit,
                    **macros,
                }
                all_products.append(product)

                status = "✓" if macros["energie_kcal"] else "○"
                print(f"  {status} {title}")

                time.sleep(random.uniform(0.4, 1.2))

            # Průběžné ukládání po každé stránce
            if save_fn and all_products:
                save_fn(all_products)

            page += 1
            if page > page_count:
                break
            time.sleep(random.uniform(1.0, 2.5))

    return all_products


def save_csv(products: list, filename: str):
    if not products:
        print("Žádné produkty k uložení.")
        return

    fieldnames = list(products[0].keys())
    path = Path(filename)
    with open(path, "w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(products)

    print(f"\nUloženo {len(products)} produktů do {path.absolute()}")


def scrape_all_categories(save_fn=None, existing=None, skip_tpncs=None):
    """Scrape ALL food categories from Tesco taxonomy."""
    all_products = list(existing or [])
    seen_tpnc = set(skip_tpncs or set())

    # Food-related superDepartments to include (skip non-food like pets, household)
    FOOD_KEYWORDS = {
        "ovoce", "zelenin", "maso", "lahůdk", "mléč", "vejce", "margarín",
        "pekárn", "trvanl", "mražen", "nápoj", "cukrovink", "sladk",
        "snack", "chipsy", "konzerv", "hotov", "přílohy", "koření",
        "omáčk", "olej", "ocet", "müsli", "cereáli", "těstovin",
        "luštěnin", "mouka", "cukr", "med", "džem", "čaj", "káva",
        "dětsk", "kojen", "velikonoc", "bio", "vegan", "bezlepk",
        "protein", "fit", "diet",
    }
    SKIP_KEYWORDS = {"mazlíč", "pejsk", "kočk", "domácnost", "drogeri",
                     "kosmetik", "hygiena", "úklid", "prací", "papír",
                     "zvíře", "domov", "zábava", "dítě", "novinky"}

    print("Načítám taxonomii...", flush=True)
    taxonomy = get_taxonomy()
    print(f"Nalezeno {len(taxonomy)} superDepartmentů")

    # Collect all department/aisle category IDs
    categories = []
    for super_dept in taxonomy:
        name = super_dept.get("name", "")
        name_lower = name.lower()

        if any(s in name_lower for s in SKIP_KEYWORDS):
            print(f"  Přeskakuji: {name}")
            continue

        children = super_dept.get("children", [])
        if not children:
            continue

        print(f"  {name}: {len(children)} podkategorií")
        for dept in children:
            dept_name = dept.get("name", "")
            # Use aisle-level if available, otherwise department
            aisles = dept.get("children", [])
            if aisles:
                for aisle in aisles:
                    if aisle.get("id"):
                        categories.append((aisle["id"], f"{name} > {dept_name} > {aisle['name']}"))
            elif dept.get("id"):
                categories.append((dept["id"], f"{name} > {dept_name}"))

    print(f"\nCelkem {len(categories)} kategorií ke stažení\n")

    for cat_idx, (cat_id, cat_path) in enumerate(categories):
        print(f"\n[{cat_idx+1}/{len(categories)}] {cat_path}")

        page = 1
        while True:
            data = browse_category(cat_id, page=page)
            results = data.get("results", [])
            page_info = data.get("pageInformation") or {}
            total = page_info.get("total") or 0
            page_size = page_info.get("pageSize") or PRODUCTS_PER_PAGE

            if not results:
                if page == 1:
                    print(f"  (prázdná)")
                break

            if page == 1:
                page_count = (total + page_size - 1) // page_size if page_size else 1
                print(f"  {total} produktů ({page_count} stránek)")

            new_count = 0
            for item in results:
                node = item.get("node", {})
                tpnc = node.get("tpnc")
                if not tpnc or tpnc in seen_tpnc:
                    continue
                seen_tpnc.add(tpnc)
                new_count += 1

                title = node.get("title", "")
                brand = node.get("brandName", "")
                gtin = node.get("gtin", "")
                price_data = node.get("price", {})
                price = price_data.get("actual")
                unit_price = price_data.get("unitPrice")
                unit = price_data.get("unitOfMeasure", "")

                detail = get_product_nutrition(tpnc)
                details = detail.get("details", {})
                nutrition_list = details.get("nutrition", [])
                macros = parse_nutrition(nutrition_list)

                product = {
                    "tpnc": tpnc,
                    "ean": gtin,
                    "nazev": title,
                    "znacka": brand,
                    "cena_kc": price,
                    "jednotkova_cena": unit_price,
                    "jednotka": unit,
                    **macros,
                }
                all_products.append(product)
                time.sleep(random.uniform(0.1, 0.3))

            print(f"  str. {page}: +{new_count} nových (celkem {len(all_products)})")

            if save_fn and all_products:
                save_fn(all_products)

            page += 1
            total_pages = (total + page_size - 1) // page_size if page_size else 1
            if page > total_pages:
                break
            time.sleep(random.uniform(0.3, 0.7))

    return all_products


def main():
    print("Tesco CZ Scraper - VŠECHNY potraviny s makronutrienty")

    # Resume from existing CSV if present
    resume_tpncs = set()
    existing = []
    csv_path = Path(OUTPUT_FILE)
    if csv_path.exists():
        with open(csv_path, "r", encoding="utf-8") as f:
            reader = csv.DictReader(f)
            for row in reader:
                existing.append(row)
                resume_tpncs.add(row.get("tpnc", ""))
        print(f"Resuming: {len(existing)} produktů už staženo")

    products = scrape_all_categories(
        save_fn=lambda p: save_csv(p, OUTPUT_FILE),
        existing=existing,
        skip_tpncs=resume_tpncs,
    )

    save_csv(products, OUTPUT_FILE)

    with_nutrition = sum(1 for p in products if p["energie_kcal"])
    print(f"\nStatistiky:")
    print(f"  Celkem produktů: {len(products)}")
    print(f"  S nutričními údaji: {with_nutrition}")
    print(f"  Bez nutričních údajů: {len(products) - with_nutrition}")


if __name__ == "__main__":
    main()
