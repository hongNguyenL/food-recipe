import psycopg
conn = psycopg.connect(host='localhost', port=5432, dbname='foodrecipe_db', user='postgres', password='postgres')
cur = conn.cursor()
cur.execute("UPDATE recipes SET image_url = 'https://images.unsplash.com/photo-1550304943-4f24f54ddde9?w=400' WHERE id = 2")
conn.commit()
cur.execute("SELECT id, title, image_url FROM recipes ORDER BY id")
for r in cur.fetchall():
    print(f'{r[0]}: {r[1]} -> {r[2]}')
conn.close()
