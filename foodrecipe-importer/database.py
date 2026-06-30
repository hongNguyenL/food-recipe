import psycopg
from psycopg.rows import dict_row

from config import Config


def get_connection(config: Config):
    conn = psycopg.connect(
        host=config.DB_HOST,
        port=config.DB_PORT,
        dbname=config.DB_NAME,
        user=config.DB_USER,
        password=config.DB_PASSWORD,
        row_factory=dict_row,
    )
    conn.autocommit = False
    return conn


def test_connection(conn) -> bool:
    try:
        with conn.cursor() as cur:
            cur.execute("SELECT NOW()")
            row = cur.fetchone()
            print(f"Connected successfully  ({row['now']})")
            return True
    except Exception as e:
        print(f"Connection failed: {e}")
        return False


def ensure_category(conn, name: str = "Imported") -> int:
    with conn.cursor() as cur:
        cur.execute("SELECT id FROM categories WHERE name = %s", (name,))
        row = cur.fetchone()
        if row:
            return row["id"]

        cur.execute(
            "INSERT INTO categories (name) VALUES (%s) RETURNING id",
            (name,),
        )
        category_id = cur.fetchone()["id"]
        conn.commit()
        return category_id


def insert_recipe(conn, title: str, category_id: int) -> int:
    with conn.cursor() as cur:
        cur.execute(
            "INSERT INTO recipes (title, category_id, created_at, updated_at) "
            "VALUES (%s, %s, NOW(), NOW()) RETURNING id",
            (title, category_id),
        )
        return cur.fetchone()["id"]


def insert_ingredients(conn, recipe_id: int, ingredients: list[str]):
    with conn.cursor() as cur:
        with cur.copy(
            "COPY ingredients (recipe_id, ingredient_text) FROM STDIN"
        ) as copy:
            for text in ingredients:
                copy.write_row((recipe_id, text))


def insert_instructions(conn, recipe_id: int, instructions: list[str]):
    with conn.cursor() as cur:
        with cur.copy(
            "COPY instructions (recipe_id, step_number, instruction_text) FROM STDIN"
        ) as copy:
            for step, text in enumerate(instructions, start=1):
                copy.write_row((recipe_id, step, text))
