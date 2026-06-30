import os
from dotenv import load_dotenv

load_dotenv()


class Config:
    DB_HOST = os.getenv("DB_HOST", "localhost")
    DB_PORT = int(os.getenv("DB_PORT", "5432"))
    DB_NAME = os.getenv("DB_NAME", "postgres")
    DB_USER = os.getenv("DB_USER", "postgres")
    DB_PASSWORD = os.getenv("DB_PASSWORD", "postgres")
    CSV_PATH = os.getenv("CSV_PATH", "recipes.csv")
    LOG_FILE = os.getenv("LOG_FILE", "importer.log")
    LOG_LEVEL = os.getenv("LOG_LEVEL", "INFO")
    BATCH_SIZE = int(os.getenv("BATCH_SIZE", "500"))
    IMPORT_LIMIT = int(os.getenv("IMPORT_LIMIT", "0")) or None
