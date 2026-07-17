"""Restore original Unsplash image URLs."""

import psycopg

ORIGINAL = {
    "Classic Bruschetta": "https://images.unsplash.com/photo-1572695157366-5e585ab2b69f?w=400",
    "Spinach Artichoke Dip": "https://images.unsplash.com/photo-1576485290814-1c72a4e8e9c6?w=400",
    "Grilled Lemon Herb Chicken": "https://images.unsplash.com/photo-1532550907401-a500c9a57435?w=400",
    "Spaghetti Carbonara": "https://images.unsplash.com/photo-1612874742237-6526221588e3?w=400",
    "Beef Stir-Fry": "https://images.unsplash.com/photo-1512058564366-18510be2db19?w=400",
    "Chocolate Lava Cake": "https://images.unsplash.com/photo-1624353365286-3f8d62daad51?w=400",
    "Classic Tiramisu": "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=400",
    "Caesar Salad": "https://images.unsplash.com/photo-1546793665-c74683f339c1?w=400",
    "Mediterranean Quinoa Bowl": "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400",
    "Tomato Basil Soup": "https://images.unsplash.com/photo-1547592166-23ac45744acd?w=400",
}

conn = psycopg.connect(
    host='localhost', port=5432, dbname='foodrecipe_db',
    user='postgres', password='postgres'
)
cur = conn.cursor()
for title, url in ORIGINAL.items():
    cur.execute('UPDATE recipes SET image_url = %s WHERE title = %s', (url, title))
    print(f'Restored: {title}')
conn.commit()
conn.close()
print('Done')
