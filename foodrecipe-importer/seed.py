"""Seed script: populates local foodrecipe_db with sample categories & recipes."""

import sys
import os
sys.path.insert(0, os.path.dirname(__file__))

from config import Config
from database import get_connection, test_connection, ensure_category, insert_recipe, insert_ingredients, insert_instructions

SEED_DATA = {
    "Appetizers": [
        {
            "title": "Classic Bruschetta",
            "image_url": "https://images.unsplash.com/photo-1572695157366-5e585ab2b69f?w=400",
            "description": "Fresh tomatoes, basil, and mozzarella on crispy toasted bread — the perfect starter.",
            "ingredients": ["1 baguette, sliced", "4 ripe tomatoes, diced", "2 cloves garlic, minced", "1/4 cup fresh basil, chopped", "1/4 cup olive oil", "Salt and pepper to taste", "1/2 cup mozzarella balls"],
            "instructions": ["Preheat oven to 400°F (200°C).", "Arrange baguette slices on a baking sheet and toast for 5 minutes.", "Rub each slice with garlic.", "Top with diced tomatoes, basil, and mozzarella.", "Drizzle with olive oil, season with salt and pepper.", "Bake for another 5 minutes until cheese melts."]
        },
        {
            "title": "Spinach Artichoke Dip",
            "image_url": "https://images.unsplash.com/photo-1576485290814-1c72a4e8e9c6?w=400",
            "description": "Creamy, cheesy dip loaded with spinach and artichokes — a party favorite.",
            "ingredients": ["1 cup frozen spinach, thawed", "1 can artichoke hearts, chopped", "8 oz cream cheese", "1/2 cup sour cream", "1/2 cup mayonnaise", "1 cup mozzarella, shredded", "1/2 cup Parmesan"],
            "instructions": ["Preheat oven to 375°F (190°C).", "Squeeze excess water from spinach.", "Mix all ingredients in a bowl.", "Transfer to a baking dish.", "Bake for 25 minutes until bubbly and golden.", "Serve warm with tortilla chips."]
        },
    ],
    "Main Course": [
        {
            "title": "Grilled Lemon Herb Chicken",
            "image_url": "https://images.unsplash.com/photo-1532550907401-a500c9a57435?w=400",
            "description": "Juicy chicken breasts marinated in lemon, garlic, and fresh herbs, grilled to perfection.",
            "ingredients": ["4 chicken breasts", "1/4 cup olive oil", "Juice of 2 lemons", "3 cloves garlic, minced", "1 tbsp fresh rosemary", "1 tbsp fresh thyme", "Salt and pepper"],
            "instructions": ["Mix olive oil, lemon juice, garlic, rosemary, thyme, salt, and pepper.", "Marinate chicken for at least 30 minutes.", "Preheat grill to medium-high heat.", "Grill chicken 6-7 minutes per side.", "Let rest 5 minutes before serving."]
        },
        {
            "title": "Spaghetti Carbonara",
            "image_url": "https://images.unsplash.com/photo-1612874742237-6526221588e3?w=400",
            "description": "Classic Roman pasta with creamy egg sauce, pancetta, and Pecorino Romano.",
            "ingredients": ["400g spaghetti", "200g pancetta", "4 eggs", "1 cup Pecorino Romano", "Freshly cracked black pepper", "Salt"],
            "instructions": ["Cook spaghetti in salted boiling water until al dente.", "While pasta cooks, fry pancetta until crispy.", "Beat eggs with Pecorino and black pepper.", "Drain pasta, reserving 1 cup pasta water.", "Toss hot pasta with pancetta.", "Pour egg mixture over pasta, tossing quickly.", "Add pasta water as needed for creamy consistency."]
        },
        {
            "title": "Beef Stir-Fry",
            "image_url": "https://images.unsplash.com/photo-1512058564366-18510be2db19?w=400",
            "description": "Quick and flavorful beef stir-fry with colorful vegetables in a savory sauce.",
            "ingredients": ["500g beef sirloin, thinly sliced", "2 bell peppers, sliced", "1 broccoli crown, florets", "3 tbsp soy sauce", "2 tbsp oyster sauce", "1 tbsp sesame oil", "2 cloves garlic", "1 tsp ginger"],
            "instructions": ["Mix soy sauce, oyster sauce, and sesame oil.", "Stir-fry beef in hot wok for 2 minutes, set aside.", "Sauté garlic and ginger for 30 seconds.", "Add vegetables and stir-fry 3 minutes.", "Return beef to wok, add sauce.", "Toss everything together for 1 minute.", "Serve over steamed rice."]
        },
    ],
    "Desserts": [
        {
            "title": "Chocolate Lava Cake",
            "image_url": "https://images.unsplash.com/photo-1624353365286-3f8d62daad51?w=400",
            "description": "Rich dark chocolate cake with a molten center — an indulgent treat.",
            "ingredients": ["200g dark chocolate", "100g butter", "2 eggs", "2 egg yolks", "1/4 cup sugar", "2 tbsp flour"],
            "instructions": ["Preheat oven to 425°F (220°C).", "Melt chocolate and butter together.", "Whisk eggs, yolks, and sugar until thick.", "Fold in chocolate mixture.", "Fold in flour gently.", "Pour into greased ramekins.", "Bake exactly 12 minutes.", "Serve immediately with ice cream."]
        },
        {
            "title": "Classic Tiramisu",
            "image_url": "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=400",
            "description": "Layered Italian dessert with espresso-soaked ladyfingers and mascarpone cream.",
            "ingredients": ["6 egg yolks", "3/4 cup sugar", "500g mascarpone", "2 cups heavy cream", "2 cups strong espresso", "3 tbsp coffee liqueur", "Ladyfingers", "Cocoa powder"],
            "instructions": ["Beat yolks and sugar until thick and pale.", "Fold in mascarpone.", "Whip cream to stiff peaks and fold in.", "Mix espresso and liqueur.", "Dip ladyfingers briefly in espresso.", "Layer dipped ladyfingers, then cream.", "Repeat layers.", "Refrigerate at least 4 hours.", "Dust with cocoa before serving."]
        },
    ],
    "Salads": [
        {
            "title": "Caesar Salad",
            "image_url": "https://images.unsplash.com/photo-1546793665-c74683f339c1?w=400",
            "description": "Crisp romaine lettuce with classic Caesar dressing, croutons, and Parmesan.",
            "ingredients": ["1 head romaine lettuce", "1/2 cup Caesar dressing", "1 cup croutons", "1/2 cup Parmesan shavings", "Grilled chicken (optional)"],
            "instructions": ["Wash and chop romaine lettuce.", "Toss with Caesar dressing.", "Top with croutons and Parmesan.", "Add grilled chicken slices if desired.", "Serve immediately."]
        },
        {
            "title": "Mediterranean Quinoa Bowl",
            "image_url": "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400",
            "description": "Healthy quinoa bowl with fresh Mediterranean vegetables and feta cheese.",
            "ingredients": ["1 cup quinoa", "1 cucumber, diced", "1 cup cherry tomatoes", "1/2 red onion", "1/2 cup kalamata olives", "1/2 cup feta cheese", "Lemon vinaigrette"],
            "instructions": ["Cook quinoa according to package directions, let cool.", "Dice cucumber, halve tomatoes, slice onion.", "Combine quinoa with vegetables.", "Add olives and crumbled feta.", "Drizzle with lemon vinaigrette.", "Toss gently and serve."]
        },
    ],
    "Soups": [
        {
            "title": "Tomato Basil Soup",
            "image_url": "https://images.unsplash.com/photo-1547592166-23ac45744acd?w=400",
            "description": "Velvety smooth tomato soup with fresh basil — comfort in a bowl.",
            "ingredients": ["6 large tomatoes", "1 onion", "3 cloves garlic", "1/4 cup fresh basil", "2 cups vegetable broth", "1/2 cup heavy cream", "Olive oil", "Salt and pepper"],
            "instructions": ["Roast tomatoes at 400°F until soft.", "Sauté onion and garlic in olive oil.", "Add roasted tomatoes and broth.", "Simmer 20 minutes.", "Blend until smooth.", "Stir in cream and basil.", "Season and serve with crusty bread."]
        },
    ],
}

def seed():
    config = Config()
    config.DB_HOST = "localhost"
    config.DB_PORT = 5432
    config.DB_NAME = "foodrecipe_db"
    config.DB_USER = "postgres"
    config.DB_PASSWORD = "postgres"

    conn = get_connection(config)
    if not test_connection(conn):
        conn.close()
        sys.exit(1)

    total = 0
    for category_name, recipes in SEED_DATA.items():
        cat_id = ensure_category(conn, category_name)
        print(f"Category '{category_name}' (id={cat_id})")
        for recipe in recipes:
            recipe_id = insert_recipe(conn, recipe["title"], cat_id)

            with conn.cursor() as cur:
                cur.execute("UPDATE recipes SET image_url = %s, description = %s WHERE id = %s",
                           (recipe["image_url"], recipe["description"], recipe_id))

            insert_ingredients(conn, recipe_id, recipe["ingredients"])
            insert_instructions(conn, recipe_id, recipe["instructions"])
            total += 1
            print(f"  Inserted recipe: {recipe['title']} (id={recipe_id})")

    conn.commit()
    conn.close()
    print(f"\nDone. Seeded {total} recipes across {len(SEED_DATA)} categories.")

if __name__ == "__main__":
    seed()
