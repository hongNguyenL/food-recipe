import json
import math


def _safe_str(val) -> str:
    if val is None:
        return ""
    if isinstance(val, float) and math.isnan(val):
        return ""
    return str(val).strip()


def _parse_json_array(raw: str) -> list[str]:
    if not raw:
        return []
    try:
        parsed = json.loads(raw)
    except json.JSONDecodeError as exc:
        raise ValueError(f"Invalid JSON: {exc}") from exc
    if not isinstance(parsed, list):
        return []
    return [i.strip() for i in parsed if i and isinstance(i, str) and i.strip()]


def parse_recipe(row: dict) -> dict:
    title = _safe_str(row.get("title"))
    ingredients_raw = _safe_str(row.get("ingredients"))
    directions_raw = _safe_str(row.get("directions"))

    ingredients = _parse_json_array(ingredients_raw)
    directions = _parse_json_array(directions_raw)

    return {
        "title": title,
        "ingredients": ingredients,
        "directions": directions,
    }
