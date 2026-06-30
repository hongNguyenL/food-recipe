import argparse
import sys
import time

import pandas as pd

from config import Config
from csv_reader import read_csv
from database import (
    ensure_category,
    get_connection,
    insert_ingredients,
    insert_instructions,
    insert_recipe,
    test_connection,
)
from logger import setup_logger
from parser import parse_recipe
from validator import validate_recipe

log = None


def _progress_display(imported: int, total=None):
    if total:
        print(f"\rImported {imported} / {total} recipes", end="", flush=True)
    else:
        print(f"\rImported {imported} recipes", end="", flush=True)


def milestone1(config: Config):
    count = 0
    for row in read_csv(config.CSV_PATH):
        if count >= 10:
            break
        try:
            recipe = parse_recipe(row)
            if not validate_recipe(recipe):
                log.warning(f"Skipped (validation failed): {recipe.get('title', 'N/A')}")
                continue
            print(f"Title: {recipe['title']}")
            print(f"  Ingredients: {len(recipe['ingredients'])}")
            print(f"  Directions:  {len(recipe['directions'])}")
            count += 1
        except Exception as e:
            log.error(f"Error processing row: {e}")
    log.info(f"Milestone 1 complete — displayed {count} recipes")


def milestone2(config: Config):
    conn = get_connection(config)
    if not test_connection(conn):
        conn.close()
        sys.exit(1)

    category_id = ensure_category(conn)
    log.info(f"Using category_id={category_id}")

    for row in read_csv(config.CSV_PATH):
        try:
            recipe = parse_recipe(row)
            if not validate_recipe(recipe):
                log.warning(f"Skipped (validation failed): {row.get('title', 'N/A')}")
                continue

            recipe_id = insert_recipe(conn, recipe["title"], category_id)
            insert_ingredients(conn, recipe_id, recipe["ingredients"])
            insert_instructions(conn, recipe_id, recipe["directions"])
            conn.commit()

            log.info(f"Inserted recipe {recipe_id}: {recipe['title']}")
            print(f"Inserted single recipe: {recipe['title']} (id={recipe_id})")
            break
        except Exception as e:
            conn.rollback()
            log.error(f"Failed to insert first recipe: {e}")
            break

    conn.close()


def _flush_batch(conn, recipes: list[dict], category_id: int) -> int:
    count = 0
    try:
        for recipe in recipes:
            recipe_id = insert_recipe(conn, recipe["title"], category_id)
            insert_ingredients(conn, recipe_id, recipe["ingredients"])
            insert_instructions(conn, recipe_id, recipe["directions"])
            count += 1
        conn.commit()
        log.info(f"Committed batch of {count} recipes")
    except Exception as e:
        conn.rollback()
        log.error(f"Batch of {len(recipes)} recipes failed, rolled back: {e}")
        return 0
    return count


def milestone3(config: Config):
    conn = get_connection(config)
    if not test_connection(conn):
        conn.close()
        sys.exit(1)

    category_id = ensure_category(conn)
    log.info(f"Using category_id={category_id}")

    total_imported = 0
    total_skipped = 0
    total_failed = 0
    start_time = time.time()

    batch: list[dict] = []
    row_count = 0
    chunk_size = 1000

    def _limit_reached():
        return config.IMPORT_LIMIT and total_imported + len(batch) >= config.IMPORT_LIMIT

    for chunk in pd.read_csv(config.CSV_PATH, chunksize=chunk_size, dtype=str):
        for _, row in chunk.iterrows():
            if _limit_reached():
                break

            row_count += 1
            try:
                recipe = parse_recipe(row.to_dict())
                if not validate_recipe(recipe):
                    total_skipped += 1
                    continue

                batch.append(recipe)

                if len(batch) >= config.BATCH_SIZE:
                    imported = _flush_batch(conn, batch, category_id)
                    total_imported += imported
                    batch.clear()
                    _progress_display(total_imported, config.IMPORT_LIMIT)
            except Exception as e:
                total_failed += 1
                log.error(f"Error processing row {row_count}: {e}")

        _progress_display(total_imported, config.IMPORT_LIMIT)
        if _limit_reached():
            break

    if batch:
        imported = _flush_batch(conn, batch, category_id)
        total_imported += imported
        _progress_display(total_imported, config.IMPORT_LIMIT)

    elapsed = time.time() - start_time
    print()
    log.info(
        f"Done. Imported={total_imported} Skipped={total_skipped} "
        f"Failed={total_failed} Elapsed={elapsed:.2f}s"
    )
    print(
        f"Final: Imported={total_imported}  Skipped={total_skipped}  "
        f"Failed={total_failed}  Time={elapsed:.2f}s"
    )
    conn.close()


def main():
    parser = argparse.ArgumentParser(description="Food Recipe CSV Importer")
    parser.add_argument(
        "--csv",
        default=None,
        help="Path to CSV file (overrides .env)",
    )
    parser.add_argument(
        "--milestone",
        type=int,
        default=3,
        choices=[1, 2, 3],
        help="1=preview, 2=single insert, 3=full import (default)",
    )
    parser.add_argument(
        "--batch-size",
        type=int,
        default=None,
        help="Recipes per commit batch (overrides .env)",
    )
    parser.add_argument(
        "--limit",
        type=int,
        default=None,
        help="Max recipes to import (overrides .env)",
    )
    args = parser.parse_args()

    config = Config()
    if args.csv:
        config.CSV_PATH = args.csv
    if args.batch_size:
        config.BATCH_SIZE = args.batch_size
    if args.limit is not None:
        config.IMPORT_LIMIT = args.limit

    global log
    log = setup_logger(config.LOG_FILE, config.LOG_LEVEL)
    log.info(f"Starting milestone {args.milestone} (CSV={config.CSV_PATH})")

    if args.milestone == 1:
        milestone1(config)
    elif args.milestone == 2:
        milestone2(config)
    else:
        milestone3(config)


if __name__ == "__main__":
    main()
