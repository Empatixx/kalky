BOT_NAME = "kaufland"
SPIDER_MODULES = ["kaufland.spiders"]
NEWSPIDER_MODULE = "kaufland.spiders"

# --- Playwright settings ---
DOWNLOAD_HANDLERS = {
    "http": "scrapy_playwright.handler.ScrapyPlaywrightDownloadHandler",
    "https": "scrapy_playwright.handler.ScrapyPlaywrightDownloadHandler",
}
TWISTED_REACTOR = "twisted.internet.asyncioreactor.AsyncioSelectorReactor"

PLAYWRIGHT_BROWSER_TYPE = "chromium"
PLAYWRIGHT_LAUNCH_OPTIONS = {
    "headless": True,
}
PLAYWRIGHT_DEFAULT_NAVIGATION_TIMEOUT = 20000
PLAYWRIGHT_MAX_PAGES_PER_CONTEXT = 8

# Block heavy resources to speed up page loads
PLAYWRIGHT_CONTEXTS = {
    "default": {
        "ignore_https_errors": True,
    },
}

# Abort requests for heavy resource types — major speedup
PLAYWRIGHT_ABORT_REQUEST = lambda req: (
    req.resource_type in ("image", "font", "media", "stylesheet")
    or any(p in req.url for p in (
        "google-analytics", "googletagmanager", "facebook", "doubleclick",
        "hotjar", "datadoghq", "adobedtm", "onetrust", "cookielaw",
        "bat.bing", "tiktok", "pinterest", "criteo", "taboola",
    ))
)

# --- Crawl settings ---
CONCURRENT_REQUESTS = 3
CONCURRENT_REQUESTS_PER_DOMAIN = 3
DOWNLOAD_DELAY = 2
RANDOMIZE_DOWNLOAD_DELAY = True
DOWNLOAD_TIMEOUT = 25
COOKIES_ENABLED = True

ROBOTSTXT_OBEY = False

USER_AGENT = (
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 "
    "(KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"
)

# AutoThrottle — backs off when server is slow or returns errors
AUTOTHROTTLE_ENABLED = True
AUTOTHROTTLE_START_DELAY = 2
AUTOTHROTTLE_MAX_DELAY = 30
AUTOTHROTTLE_TARGET_CONCURRENCY = 2.0

# Retry with backoff on 429
RETRY_TIMES = 3
RETRY_HTTP_CODES = [500, 502, 503, 504, 408, 429]
RETRY_PRIORITY_ADJUST = -1

# --- Output ---
# Using JSONL (one JSON object per line) — crash-safe, no corruption on kill
FEEDS = {
    "products.jsonl": {
        "format": "jsonlines",
        "encoding": "utf-8",
        "overwrite": False,
        "fields": [
            "barcode", "name", "energy_kcal_100g",
            "protein_100g", "fat_100g", "carbs_100g",
            "serving_size", "image_url",
        ],
    },
    "products_full.jsonl": {
        "format": "jsonlines",
        "encoding": "utf-8",
        "overwrite": False,
    },
}

ITEM_PIPELINES = {
    "kaufland.pipelines.CleanNutritionPipeline": 100,
    "kaufland.pipelines.DropIncompleteItemsPipeline": 200,
}

LOG_LEVEL = "INFO"

REQUEST_FINGERPRINTER_IMPLEMENTATION = "2.7"
