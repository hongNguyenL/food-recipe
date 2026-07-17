"""Fast bulk importer: reads CSV and bulk-inserts to Supabase in large batches."""

import json
import math
import time
import pandas as pd
import psycopg
from psycopg.rows import dict_row

DB_CONFIG = {
    "host": "db.dqulxqootxqnapisxzju.supabase.co",
    "port": 5432,
    "dbname": "postgres",
    "user": "postgres",
    "password": "lequyhongnguyen1510",
}

CSV_PATH = "data/recipes_data.csv"
BATCH_SIZE = 2000
CHUNK_SIZE = 5000
TOTAL_LIMIT = 50000


def safe_str(val) -> str:
    if val is None:
        return ""
    if isinstance(val, float) and math.isnan(val):
        return ""
    return str(val).strip()


def parse_json_array(raw: str) -> list[str]:
    if not raw:
        return []
    try:
        parsed = json.loads(raw)
    except (json.JSONDecodeError, TypeError):
        return []
    if not isinstance(parsed, list):
        return []
    return [i.strip() for i in parsed if i and isinstance(i, str) and i.strip()]


def ensure_category(conn) -> int:
    name = "Imported"
    with conn.cursor() as cur:
        cur.execute("SELECT id FROM categories WHERE name = %s", (name,))
        row = cur.fetchone()
        if row:
            return row["id"]
        cur.execute("INSERT INTO categories (name) VALUES (%s) RETURNING id", (name,))
        cat_id = cur.fetchone()["id"]
        conn.commit()
        return cat_id


def main():
    conn = psycopg.connect(**DB_CONFIG, row_factory=dict_row)
    conn.autocommit = False

    cat_id = ensure_category(conn)
    print(f"Category ID: {cat_id}")

    total = 0
    start = time.time()

    for chunk_i, chunk in enumerate(pd.read_csv(CSV_PATH, chunksize=CHUNK_SIZE, dtype=str)):
        if total >= TOTAL_LIMIT:
            break

        batch_recipes = []
        for _, row in chunk.iterrows():
            if total + len(batch_recipes) >= TOTAL_LIMIT:
                break
            title = safe_str(row.get("title"))
            if not title:
                continue
            ingredients = parse_json_array(row.get("ingredients"))
            directions = parse_json_array(row.get("directions"))
            if not ingredients or not directions:
                continue
            batch_recipes.append((title, ingredients, directions))

            if len(batch_recipes) >= BATCH_SIZE:
                total += flush_batch(conn, batch_recipes, cat_id)
                elapsed = time.time() - start
                rate = total / elapsed * 60
                print(f"Imported {total}/{TOTAL_LIMIT}  ({rate:.0f}/min)")
                batch_recipes = []

        if batch_recipes:
            total += flush_batch(conn, batch_recipes, cat_id)
            elapsed = time.time() - start
            rate = total / elapsed * 60
            print(f"Imported {total}/{TOTAL_LIMIT}  ({rate:.0f}/min)")

    elapsed = time.time() - start
    print(f"\nDone. Imported {total} recipes in {elapsed:.0f}s ({total/elapsed*60:.0f}/min)")
    conn.close()


def flush_batch(conn, batch, cat_id) -> int:
    try:
        titles = [r[0] for r in batch]
        with conn.cursor() as cur:
            # Multi-row INSERT with RETURNING
            values_clause = ", ".join(f"(%s, {cat_id}, NOW(), NOW())" for _ in titles)
            flat_params = []
            for t in titles:
                flat_params.extend([t])
            cur.execute(
                f"INSERT INTO recipes (title, category_id, created_at, updated_at) VALUES {values_clause} RETURNING id",
                flat_params,
            )
            ids = [row["id"] for row in cur.fetchall()]

        # Bulk insert ingredients and instructions using COPY
        with conn.cursor() as cur:
            with cur.copy("COPY ingredients (recipe_id, ingredient_text) FROM STDIN") as copy:
                for (_, ingredients, _), rid in zip(batch, ids):
                    for ing in ingredients:
                        copy.write_row((rid, ing))

        with conn.cursor() as cur:
            with cur.copy("COPY instructions (recipe_id, step_number, instruction_text) FROM STDIN") as copy:
                for (_, _, instructions), rid in zip(batch, ids):
                    for step, text in enumerate(instructions, start=1):
                        copy.write_row((rid, step, text))

        conn.commit()
        return len(batch)
    except Exception as e:
        conn.rollback()
        print(f"Batch of {len(batch)} failed: {e}")
        return 0


if __name__ == "__main__":
    main()
