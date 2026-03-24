"""
Kaufland.cz marketplace product scraper.

Usage:
    cd scraper
    scrapy crawl kaufland_products
    scrapy crawl kaufland_products -a queries="jogurt,mleko,chleba"
    scrapy crawl kaufland_products -a max_pages=10
"""
import json
import os
import re
from urllib.parse import quote, urljoin

import scrapy
from scrapy_playwright.page import PageMethod

from kaufland.items import ProductItem

DEFAULT_QUERIES = [
    # Dairy
    "jogurt", "mléko", "sýr", "tvaroh", "máslo", "smetana", "kefír",
    "cottage", "mascarpone", "mozzarella", "parmazán", "eidam", "gouda",
    "camembert", "brie", "ricotta", "feta", "cream cheese",
    # Bread & bakery
    "chléb", "rohlík", "bageta", "toast", "knäckebrot", "tortilla",
    "croissant", "celozrnný chléb", "žitný chléb",
    # Meat & deli
    "šunka", "kuřecí", "salám", "párek", "slanina", "hovězí", "vepřové",
    "krůtí", "klobása", "mortadela", "prosciutto", "chorizo",
    # Fish
    "tuňák", "losos", "sardinky", "treska", "rybí filé", "krevety",
    # Cereals & breakfast
    "müsli", "ovesné vločky", "cereálie", "cornflakes", "granola",
    "proteinový", "chia", "lněné semínko",
    # Pasta & rice & grains
    "těstoviny", "špagety", "rýže", "kuskus", "bulgur", "quinoa",
    "pohanka", "jáhly", "nudle",
    # Eggs
    "vejce",
    # Oils & fats
    "olej", "olivový olej", "kokosový olej", "ghí",
    # Baking
    "mouka", "droždí", "prášek do pečiva", "vanilkový cukr", "kakao",
    # Sweets & snacks
    "čokoláda", "sušenky", "chipsy", "oříšky", "arašídy", "mandle",
    "tyčinka", "müsli tyčinka", "bonbóny", "gumové medvídky",
    "křupky", "popcorn", "pistácie", "kešu",
    # Spreads & condiments
    "med", "džem", "nutella", "arašídové máslo", "marmeláda",
    "kečup", "majonéza", "hořčice", "sójová omáčka", "tatarská omáčka",
    "pesto", "ajvar", "hummus", "guacamole",
    # Canned & preserved
    "konzerva", "fazole", "hrášek", "kukuřice", "rajčata pasírovaná",
    "olivy", "kapary", "okurky", "zelí",
    # Beverages
    "džus", "čaj", "káva", "kakao nápoj", "voda minerální",
    "limonáda", "energy drink", "smoothie", "ovesný nápoj",
    "mandlové mléko", "sójové mléko", "kokosové mléko",
    # Frozen
    "zmrzlina", "pizza", "mražená zelenina", "mražené ovoce",
    "řízek", "hranolky", "krokety",
    # Seasonings
    "cukr", "sůl", "koření", "pepř", "paprika mletá", "skořice",
    "bazalka", "oregano", "kurkuma",
    # Sauces
    "omáčka", "bešamel", "rajčatová omáčka", "BBQ omáčka", "worcester",
    # Baby food & health
    "dětská výživa", "proteinový nápoj", "proteinová tyčinka",
    # Alcohol (for completeness — has calories)
    "pivo", "víno", "prosecco",
    # Misc
    "tofu", "tempeh", "seitan", "veganský", "bezlepkový",
    "želatina", "pudink", "kompot",
]


class KauflandProductsSpider(scrapy.Spider):
    name = "kaufland_products"
    allowed_domains = ["www.kaufland.cz"]

    def __init__(self, queries=None, max_pages=5, *args, **kwargs):
        super().__init__(*args, **kwargs)
        if queries:
            self.search_queries = [q.strip() for q in queries.split(",")]
        else:
            self.search_queries = DEFAULT_QUERIES
        self.max_pages = int(max_pages)
        self.seen_urls = self._load_existing_urls()

    def start_requests(self):
        for query in self.search_queries:
            url = f"https://www.kaufland.cz/s/?search_value={quote(query)}"
            self.logger.info(f"Starting search: {query}")
            yield scrapy.Request(
                url,
                callback=self.parse_search,
                meta={
                    "playwright": True,
                    "playwright_page_methods": [
                        PageMethod("wait_for_selector", "a[href*='/product/']", timeout=15000),
                    ],
                    "query": query,
                    "page_num": 1,
                },
                errback=self.errback_log,
            )

    def parse_search(self, response):
        query = response.meta["query"]
        page_num = response.meta["page_num"]

        # Extract product links
        product_links = response.css("a[href*='/product/']::attr(href)").getall()
        product_links = list(dict.fromkeys(product_links))  # dedupe preserving order
        self.logger.info(f"[{query}] page {page_num}: found {len(product_links)} product links")

        for href in product_links:
            full_url = urljoin("https://www.kaufland.cz", href.split("?")[0])
            if full_url in self.seen_urls:
                continue
            self.seen_urls.add(full_url)

            yield scrapy.Request(
                full_url,
                callback=self.parse_product,
                meta={
                    "playwright": True,
                    "playwright_page_methods": [
                        PageMethod("wait_for_selector", "main", timeout=15000),
                    ],
                },
                errback=self.errback_log,
            )

        # URL-based pagination (no need to hold page open)
        if page_num < self.max_pages:
            next_page_url = f"https://www.kaufland.cz/s/?search_value={quote(query)}&page={page_num + 1}"
            yield scrapy.Request(
                next_page_url,
                callback=self.parse_search,
                meta={
                    "playwright": True,
                    "playwright_page_methods": [
                        PageMethod("wait_for_selector", "a[href*='/product/']", timeout=15000),
                    ],
                    "query": query,
                    "page_num": page_num + 1,
                },
                errback=self.errback_log,
            )

    def parse_product(self, response):
        item = ProductItem()
        item["kaufland_url"] = response.url

        # Product name from h1
        h1 = response.css("h1::text").get("")
        item["name"] = h1.strip()

        # Structured data from dt/dd pairs
        dt_dd = self._parse_dt_dd(response)

        # EAN barcode
        item["barcode"] = dt_dd.get("Kód EAN", "").strip().strip('"')

        # Weight
        item["serving_size"] = dt_dd.get("Hmotnost", "") or dt_dd.get("Kapacita:", "")

        # Brand
        item["brand"] = dt_dd.get("Výrobce", "").strip()

        # Ingredients
        item["ingredients"] = dt_dd.get("Seznam přísad", "")

        # Allergens
        item["allergens"] = dt_dd.get("Název typu alergenu:", "")

        # --- Nutrition: try structured dt/dd first ---
        item["energy_kcal_100g"] = self._parse_number(
            dt_dd.get("Energetická hodnota (na 100 g):", ""), prefer_kcal=True
        )
        item["protein_100g"] = self._parse_number(dt_dd.get("Obsah bílkovin (na 100 g):", ""))
        item["fat_100g"] = self._parse_number(dt_dd.get("Obsah tuku (na 100 g):", ""))
        item["carbs_100g"] = self._parse_number(dt_dd.get("Obsah uhlohydrátů (na 100 g):", ""))

        # --- Fallback 1: "Deklarovaná nutriční hodnota" block ---
        if not item["energy_kcal_100g"]:
            nutri_text = dt_dd.get("Deklarovaná nutriční hodnota", "")
            if nutri_text:
                self._fill_from_nutri_text(item, nutri_text)

        # --- Fallback 2: page body text ---
        if not item["energy_kcal_100g"]:
            body_text = " ".join(response.css("main ::text").getall())
            self._fill_from_nutri_text(item, body_text)

        # Product image (from <meta> or <img> tag)
        img = response.css('meta[property="og:image"]::attr(content)').get()
        if not img:
            img = response.css('img[alt*="Obrázek produktu"]::attr(src)').get()
        if not img:
            img = response.css("main img::attr(src)").get()
        item["image_url"] = img

        self.logger.info(
            f"Scraped: {item.get('name', '?')[:50]} | "
            f"EAN={item.get('barcode', '')} | "
            f"kcal={item.get('energy_kcal_100g', 0)}"
        )

        yield item

    @staticmethod
    def _load_existing_urls():
        """Pre-seed seen_urls from existing JSONL to skip already-scraped products."""
        urls = set()
        for path in ("products_full.jsonl",):
            if not os.path.exists(path):
                continue
            with open(path) as f:
                for line in f:
                    try:
                        item = json.loads(line)
                        if item.get("kaufland_url"):
                            urls.add(item["kaufland_url"])
                    except json.JSONDecodeError:
                        continue
        if urls:
            print(f"[resume] Loaded {len(urls)} already-scraped URLs, will skip them")
        return urls

    def errback_log(self, failure):
        self.logger.warning(f"Request failed: {failure.request.url}: {failure.value}")

    @staticmethod
    def _parse_dt_dd(response):
        """Extract all dt/dd pairs into a dict."""
        result = {}
        current_key = None
        for el in response.css("dt, dd"):
            tag = el.root.tag
            texts = el.css("::text").getall()
            text = " ".join(t.strip() for t in texts if t.strip())
            if tag == "dt":
                current_key = text
            elif tag == "dd" and current_key:
                result[current_key] = text
                current_key = None
        return result

    @staticmethod
    def _parse_number(text, prefer_kcal=False):
        if not text:
            return 0.0
        text = str(text).replace(",", ".")
        if prefer_kcal:
            m = re.search(r"(\d+\.?\d*)\s*kcal", text)
            if m:
                return float(m.group(1))
        m = re.search(r"(\d+\.?\d*)", text)
        return float(m.group(1)) if m else 0.0

    @staticmethod
    def _extract(text, pattern):
        if not text:
            return 0.0
        text = text.replace(",", ".")
        m = re.search(pattern, text)
        return float(m.group(1)) if m else 0.0

    @classmethod
    def _fill_from_nutri_text(cls, item, text):
        item["energy_kcal_100g"] = item.get("energy_kcal_100g") or cls._extract(text, r"(\d+[.]?\d*)\s*kcal")
        item["protein_100g"] = item.get("protein_100g") or cls._extract(text, r"[Bb]ílkoviny[:\s]*(\d+[.]?\d*)")
        item["fat_100g"] = item.get("fat_100g") or cls._extract(text, r"[Tt]uky[:\s]*(\d+[.]?\d*)")
        item["carbs_100g"] = item.get("carbs_100g") or cls._extract(text, r"[Ss]acharidy[:\s]*(\d+[.]?\d*)")
