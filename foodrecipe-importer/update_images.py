"""Update recipe images to use reliable placehold.co URLs."""

import psycopg

conn = psycopg.connect(
    host='localhost', port=5432, dbname='foodrecipe_db',
    user='postgres', password='postgres'
)
cur = conn.cursor()
cur.execute('SELECT id, title FROM recipes ORDER BY id')
rows = cur.fetchall()
for id_, title in rows:
    slug = title.lower().replace(' ', '+').replace("'", '')[:30]
    url = f'https://placehold.co/600x400/16a34a/ffffff?text={slug}'
    cur.execute('UPDATE recipes SET image_url = %s WHERE id = %s', (url, id_))
    print(f'Updated {id_}: {title}')
conn.commit()
conn.close()
print('Done')
