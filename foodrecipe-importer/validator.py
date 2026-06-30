def validate_recipe(recipe: dict) -> bool:
    if not recipe.get("title"):
        return False
    if not recipe.get("ingredients"):
        return False
    if not recipe.get("directions"):
        return False
    return True
