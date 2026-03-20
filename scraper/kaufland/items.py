import scrapy


class ProductItem(scrapy.Item):
    """Matches the backend's Product schema for direct JSON import."""
    barcode = scrapy.Field()          # EAN code
    name = scrapy.Field()             # Product name
    energy_kcal_100g = scrapy.Field() # kcal per 100g
    protein_100g = scrapy.Field()     # grams per 100g
    fat_100g = scrapy.Field()         # grams per 100g
    carbs_100g = scrapy.Field()       # grams per 100g
    serving_size = scrapy.Field()     # e.g. "450 g"
    image_url = scrapy.Field()        # product image URL
    # Extra fields for enrichment
    brand = scrapy.Field()
    ingredients = scrapy.Field()
    allergens = scrapy.Field()
    kaufland_url = scrapy.Field()
