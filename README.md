# Food Recipe Platform

A full-stack recipe management platform with user authentication, recipe browsing/searching, ratings, comments, favorites, and an admin dashboard. Built with Spring Boot 4 + React 19 + TypeScript.

## Tech Stack

### Backend
- **Java 21** with **Spring Boot 4.1.0**
- **Spring Security** + **JWT** (jjwt 0.12.6) for authentication
- **Spring Data JPA** + **Hibernate 7** (PostgreSQL)
- **Redis** caching (production), simple in-memory (dev)
- **MapStruct** + **Lombok**
- **SpringDoc OpenAPI 2.8.6** (Swagger UI)
- **Spring Actuator** + **Micrometer** + **Prometheus**
- **Logstash Logback Encoder** (JSON logging in prod)

### Frontend
- **React 19.2.7** + **TypeScript 6** + **Vite 8**
- **React Router DOM 7** (lazy-loaded routes)
- **TanStack React Query 5** (server state)
- **React Hook Form 7** + **Zod 4** (form validation)
- **Tailwind CSS 4**
- **Lucide React** (icons) + **Recharts 3** (charts)
- **Axios** with JWT interceptor

### Importer
- **Python 3** + **pandas** + **psycopg 3**
- ETL pipeline for Kaggle recipe CSV dataset

## Project Structure

```
food-recipe/
├── foodrecipe/                  # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/nguyen/foodrecipe/
│   │   │   │   ├── audit/            # Admin audit logging
│   │   │   │   ├── config/           # Security, CORS, Cache, OpenAPI
│   │   │   │   ├── controller/       # REST controllers
│   │   │   │   ├── dto/              # Java records (request/response)
│   │   │   │   ├── entity/           # JPA entities
│   │   │   │   ├── exception/        # Custom exceptions + handler
│   │   │   │   ├── mapper/           # MapStruct mappers
│   │   │   │   ├── repository/       # Spring Data JPA repositories
│   │   │   │   ├── security/         # JWT, UserDetailsService
│   │   │   │   └── service/          # Business logic layer
│   │   │   └── resources/
│   │   │       └── application.yml   # Config (dev/prod profiles)
│   │   └── test/
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── pom.xml
│
├── frontend/
│   └── food-recipe-frontend/     # React frontend
│       └── src/
│           ├── api/              # Axios API modules
│           ├── components/       # Reusable UI components
│           ├── hooks/            # Auth + Theme context
│           ├── layouts/          # Main + Admin layouts
│           ├── pages/            # Route pages
│           ├── routes/           # Route definitions
│           └── types/            # TypeScript interfaces
│
├── foodrecipe-importer/          # Python ETL importer
│   ├── main.py
│   ├── config.py
│   ├── csv_reader.py
│   ├── parser.py
│   ├── validator.py
│   ├── database.py
│   ├── logger.py
│   └── seed.py
│
└── README.md
```

## Getting Started

### Prerequisites
- Java 21+
- Node.js 20+
- Python 3.9+
- PostgreSQL 16
- Docker (optional, for Redis in prod)

### Backend Setup

```bash
cd foodrecipe

# Configure database (default: postgres/postgres@localhost:5432/foodrecipe_db)
# Edit application.yml or set env vars:
#   DB_URL=jdbc:postgresql://localhost:5432/foodrecipe_db
#   DB_USERNAME=postgres
#   DB_PASSWORD=postgres

# Run with dev profile (auto schema update)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Frontend Setup

```bash
cd frontend/food-recipe-frontend
npm install
npm run dev     # Starts on http://localhost:5173
```

### Importer Setup

```bash
cd foodrecipe-importer
pip install -r requirements.txt

# Preview first 10 recipes
python main.py --milestone 1

# Full import
python main.py

# Seed sample data
python seed.py
```

### Docker (Production)

```bash
cd foodrecipe
docker compose up -d
```

This starts Redis + the Spring Boot backend (prod profile).

## API Overview

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/api/auth/register` | Public | Register new user |
| POST | `/api/auth/login` | Public | Login, returns JWT |
| GET | `/api/recipes` | Public | Paginated recipe list |
| GET | `/api/recipes/{id}` | Public | Recipe details with stats |
| GET | `/api/recipes/search` | Public | Advanced search (keyword, category, ingredient) |
| GET | `/api/recipes/popular` | Public | Popular recipes (score = rating*3 + fav*2 + comment*1) |
| GET | `/api/recipes/top-rated` | Public | Top rated by avg rating |
| GET | `/api/recipes/latest` | Public | Newest recipes |
| GET | `/api/recipes/{id}/similar` | Public | Similar recipes (up to 10) |
| POST/DELETE | `/api/recipes/{id}/favorite` | Auth | Toggle favorite |
| POST | `/api/recipes/{id}/rating` | Auth | Rate recipe (1-5, upsert) |
| POST | `/api/recipes/{id}/comments` | Auth | Add comment |
| PUT/DELETE | `/api/comments/{id}` | Auth | Edit/delete own comment |
| GET | `/api/users/me` | Auth | Current user profile |
| GET | `/api/users/me/favorites` | Auth | User's favorites |
| GET | `/api/categories` | Public | List categories |
| GET | `/api/categories/{id}/recipes` | Public | Recipes by category |
| GET | `/api/admin/dashboard` | ADMIN | Platform dashboard stats |
| CRUD | `/api/admin/recipes/**` | ADMIN | Recipe management |
| CRUD | `/api/admin/categories/**` | ADMIN | Category management |
| GET/PUT/PATCH | `/api/admin/users/**` | ADMIN | User management |
| GET/DELETE | `/api/admin/comments/**` | ADMIN | Comment moderation |

Swagger UI available at `http://localhost:8080/swagger-ui.html`

## Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_PROFILES_ACTIVE` | `dev` | Active profile |
| `DB_URL` | `jdbc:postgresql://localhost:5432/foodrecipe_db` | PostgreSQL JDBC URL |
| `DB_USERNAME` | `postgres` | Database user |
| `DB_PASSWORD` | `postgres` | Database password |
| `JWT_SECRET_KEY` | (built-in) | JWT signing key (256-bit) |
| `JWT_EXPIRATION_MS` | `86400000` | JWT expiry (24h) |
| `PORT` | `8080` | Server port |

### Profiles

- **dev** — Auto schema update, SQL logging, simple cache, console logging
- **prod** — Schema validate, Redis cache, JSON logging, actuator endpoints, optimized pool

## Features

- **JWT Authentication** — Stateless, Bearer token, 24h expiry
- **Advanced Search** — PostgreSQL trigram indexes for keyword matching
- **Popularity Algorithm** — Weighted score combining ratings, favorites, and comments
- **Similar Recipe Discovery** — Based on shared categories and ingredients
- **Caching** — Redis (prod) with 1h TTL for recipes, categories, and popular lists
- **Admin Dashboard** — User management, recipe/category CRUD, comment moderation, platform stats
- **Admin Audit Logging** — All admin actions logged as structured events
- **Bulk Data Import** — Python ETL with PostgreSQL COPY protocol for high-performance inserts
- **Dark/Light Theme** — Frontend supports theme switching
- **Responsive UI** — Tailwind CSS, mobile-friendly layouts
