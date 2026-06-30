# Food Recipe Dataset Importer

Production-quality ETL pipeline that imports a Kaggle Food Recipe CSV into an existing PostgreSQL (Supabase) database.

## Schema

The database schema (categories, recipes, ingredients, instructions) is managed by a Spring Boot / JPA application. **This tool never creates or modifies tables.**

## Setup

```bash
# Create virtual environment
python -m venv .venv

# Activate
.venv\Scripts\activate    # Windows
source .venv/bin/activate  # Linux / macOS

# Install dependencies
pip install -r requirements.txt

# Configure environment
cp .env.example .env
```

Edit `.env` with your Supabase credentials.

## Usage

```bash
# Show help
python main.py --help

# Milestone 1 — Preview first 10 recipes (no DB insert)
python main.py --csv recipes.csv --milestone 1

# Milestone 2 — Insert a single recipe into PostgreSQL
python main.py --csv recipes.csv --milestone 2

# Milestone 3 — Full batch import (default)
python main.py --csv recipes.csv

# Override batch size
python main.py --csv recipes.csv --batch-size 250
```

## Dataset

Download the Kaggle Food Recipe dataset and place the CSV anywhere. Point `--csv` to its location.

Expected CSV columns:

| Column      | Description                        |
|-------------|------------------------------------|
| title       | Recipe title                       |
| ingredients | JSON array of ingredient strings   |
| directions  | JSON array of instruction strings  |

## Architecture

```
main.py         — CLI entry point & orchestrator
config.py       — Environment / configuration
database.py     — PostgreSQL connection & helpers
csv_reader.py   — CSV reading (pandas, chunked)
parser.py       — JSON parsing for ingredients / directions
validator.py    — Record validation rules
logger.py       — Logging setup (file + console)
```

## Project Structure

```
foodrecipe-importer/
├── .env.example
├── .gitignore
├── README.md
├── requirements.txt
├── main.py
├── config.py
├── database.py
├── csv_reader.py
├── parser.py
├── validator.py
└── logger.py
```
