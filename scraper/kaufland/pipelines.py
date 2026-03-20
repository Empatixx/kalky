import re
from itemadapter import ItemAdapter


class CleanNutritionPipeline:
    """Parse and clean nutrition values to floats."""

    _num_re = re.compile(r"[\d]+[,.]?[\d]*")

    def process_item(self, item, spider):
        adapter = ItemAdapter(item)

        for field in ("energy_kcal_100g", "protein_100g", "fat_100g", "carbs_100g"):
            raw = adapter.get(field)
            if raw is None:
                continue
            if isinstance(raw, (int, float)):
                continue
            m = self._num_re.search(str(raw).replace(",", "."))
            adapter[field] = float(m.group()) if m else 0.0

        # Clean serving_size
        serving = adapter.get("serving_size")
        if serving:
            adapter["serving_size"] = serving.strip()

        # Clean barcode
        barcode = adapter.get("barcode")
        if barcode:
            adapter["barcode"] = str(barcode).strip().strip('"')

        return item


class DropIncompleteItemsPipeline:
    """Drop items without a name or without any nutrition data."""

    def process_item(self, item, spider):
        adapter = ItemAdapter(item)
        name = adapter.get("name")
        if not name or not name.strip():
            raise DropItem(f"Missing name: {item}")

        kcal = adapter.get("energy_kcal_100g", 0)
        protein = adapter.get("protein_100g", 0)
        if not kcal and not protein:
            spider.logger.warning(f"No nutrition data for: {name}")
            # Still keep it — might be useful for name/barcode mapping

        return item
