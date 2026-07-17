# Food Recipe API

Base URL: `http://localhost:8080`

Every response is wrapped in a standard envelope:

```json
{
  "success": true,
  "message": "Human-readable message",
  "data": { ... },
  "errors": null
}
```

Error response:

```json
{
  "success": false,
  "message": "Error description",
  "data": null,
  "errors": { "fieldName": "Validation error message" }
}
```

HTTP status codes: `200` OK, `201` Created, `400` Bad Request, `401` Unauthorized, `403` Forbidden, `404` Not Found, `409` Conflict, `500` Server Error.

---

## Authentication

JWT Bearer token. Header: `Authorization: Bearer <accessToken>`. Expires in 24h.

**Public endpoints** (no auth): all `GET /api/recipes/**`, `GET /api/categories/**`, `POST /api/auth/**`, Swagger, `/actuator/health`, `/actuator/info`.

**Authenticated endpoints** (any role): require `Authorization` header. Returns `401` if missing/expired.

**Admin endpoints** (`ROLE_ADMIN`): `/api/admin/**`, most `/actuator/**` paths.

---

## Auth Endpoints

### POST /api/auth/register

```json
{
  "username": "john123",
  "email": "john@example.com",
  "password": "MyStr0ng!"
}
```

Response `201`:

```json
{
  "success": true,
  "message": "User registered successfully",
  "data": { "id": 1, "username": "john123", "email": "john@example.com", "role": "USER" }
}
```

Constraints: username 3-30 chars, email valid format, password min 8 chars with uppercase + lowercase + digit.

### POST /api/auth/login

```json
{
  "usernameOrEmail": "john123",
  "password": "MyStr0ng!"
}
```

Response `200`:

```json
{
  "success": true,
  "message": "Login successful",
  "data": { "accessToken": "eyJ...", "tokenType": "Bearer", "expiresIn": 86400000 }
}
```

Store the `accessToken` and send it with every authenticated request.

---

## Pagination

Paginated endpoints accept query params: `?page=0&size=20&sort=title,asc`.

Response `data` is a Spring Page object:

```json
{
  "success": true,
  "message": "...",
  "data": {
    "content": [],
    "page": 0,
    "size": 20,
    "totalElements": 142,
    "totalPages": 8,
    "numberOfElements": 20,
    "first": true,
    "last": false,
    "empty": false
  }
}
```

Max page size: 100. Default: 20.

---

## Recipe Endpoints (Public GET, Authenticated POST/DELETE)

### GET /api/recipes

Query: `?keyword=chicken&page=0&size=20&sort=title,asc`

Response: `Page<RecipeSummaryResponse>`

```json
{ "id": 1, "title": "Chicken Soup", "imageUrl": "...", "categoryName": "Soups" }
```

Sort fields: `title` (default), `createdAt`, `averageRating`, `favoriteCount`, `commentCount`.

### GET /api/recipes/{id}

Response: `RecipeDetailResponse`

```json
{
  "id": 1,
  "title": "Chicken Soup",
  "imageUrl": "https://...",
  "description": "A warm hearty soup",
  "category": { "id": 1, "name": "Soups" },
  "ingredients": [
    { "id": 1, "ingredientText": "Chicken breast" }
  ],
  "instructions": [
    { "id": 1, "stepNumber": 1, "instructionText": "Boil water" }
  ],
  "favoriteCount": 42,
  "averageRating": 4.5,
  "totalRatings": 100,
  "totalComments": 15
}
```

### GET /api/recipes/search

Query: `?keyword=chicken&categoryId=1&ingredient=garlic&page=0&size=20&sort=title`

Response: `Page<SearchRecipeResponse>`

```json
{
  "id": 1,
  "title": "Chicken Soup",
  "imageUrl": "...",
  "categoryName": "Soups",
  "createdAt": "2024-01-15T10:30:00",
  "averageRating": 4.5,
  "favoriteCount": 42,
  "commentCount": 15
}
```

Sort fields: `title` (default), `createdAt`, `averageRating`, `favoriteCount`, `commentCount`.

### GET /api/recipes/popular

Query: `?page=0&size=20`

Response: `Page<PopularRecipeResponse>`

```json
{
  "id": 1,
  "title": "Chicken Soup",
  "imageUrl": "...",
  "categoryName": "Soups",
  "averageRating": 4.5,
  "favoriteCount": 42,
  "commentCount": 15,
  "popularityScore": 13.5
}
```

Score = rating * 3 + favorites * 2 + comments * 1. Sorted descending.

### GET /api/recipes/top-rated

Query: `?page=0&size=20`

Response: `Page<SearchRecipeResponse>` (sorted by avg rating desc).

### GET /api/recipes/latest

Query: `?page=0&size=20`

Response: `Page<RecipeSummaryResponse>` (sorted by createdAt desc).

### GET /api/recipes/{id}/similar

Response: `List<SimilarRecipeResponse>` (max 10)

```json
{ "id": 2, "title": "Chicken Noodle Soup", "imageUrl": "...", "categoryName": "Soups", "averageRating": 4.2 }
```

### POST /api/recipes/{id}/favorite (Auth)

Response `200`: void. Returns `409` if already favorited.

### DELETE /api/recipes/{id}/favorite (Auth)

Response `200`: void.

### POST /api/recipes/{id}/rating (Auth)

```json
{ "rating": 5 }
```

1-5. Upserts (one rating per user per recipe). Response: `RatingResponse` `{ "id": 1, "recipeId": 1, "rating": 5 }`.

### GET /api/recipes/{id}/rating

Response: `RecipeRatingResponse`

```json
{ "averageRating": 4.5, "totalRatings": 100, "currentUserRating": 5 }
```

`currentUserRating` is null if anonymous user.

### POST /api/recipes/{id}/comments (Auth)

```json
{ "content": "Delicious recipe!" }
```

Max 1000 chars. Response: `CommentResponse`.

### GET /api/recipes/{id}/comments

Query: `?page=0&size=20`

Response: `Page<CommentResponse>` (newest first)

```json
{
  "id": 1,
  "recipeId": 1,
  "userId": 1,
  "username": "john123",
  "content": "Delicious!",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": null
}
```

---

## Category Endpoints (Public)

### GET /api/categories

Response: `List<CategoryResponse>`

```json
[{ "id": 1, "name": "Soups" }]
```

### GET /api/categories/{id}/recipes

Query: `?page=0&size=20&sort=title,asc`

Response: `Page<RecipeSummaryResponse>`. Sort fields: `title` (default), `createdAt`.

---

## Comment Endpoints (Auth)

### PUT /api/comments/{id}

```json
{ "content": "Updated comment" }
```

Only the author can edit. Response: `CommentResponse`.

### DELETE /api/comments/{id}

Author or admin can delete. Response: void.

---

## User Endpoints (Auth)

### GET /api/users/me

Response: `UserResponse` `{ "id": 1, "username": "john123", "email": "john@example.com", "role": "USER" }`.

### GET /api/users/me/favorites

Query: `?page=0&size=20`

Response: `Page<RecipeSummaryResponse>`.

---

## Admin Endpoints (ROLE_ADMIN)

### GET /api/admin/recipes

Query: `?keyword=chicken&page=0&size=20&sort=title`

Response: `Page<RecipeSummaryResponse>`.

### GET /api/admin/recipes/{id}

Response: `RecipeDetailResponse`.

### POST /api/admin/recipes

```json
{
  "title": "New Recipe",
  "imageUrl": "https://...",
  "description": "Description text",
  "categoryId": 1,
  "ingredients": ["Chicken", "Garlic"],
  "instructions": [{ "stepNumber": 1, "instructionText": "Cook" }]
}
```

Response: `RecipeResponse` (includes createdAt, updatedAt).

### PUT /api/admin/recipes/{id}

Same body as POST. Response: `RecipeResponse`.

### DELETE /api/admin/recipes/{id}

Response: void.

### GET /api/admin/categories

Response: `List<CategoryResponse>`.

### GET /api/admin/categories/{id}

Response: `CategoryResponse`.

### POST /api/admin/categories

```json
{ "name": "New Category" }
```

Response: `CategoryResponse`. Duplicate name returns `409`.

### PUT /api/admin/categories/{id}

```json
{ "name": "Updated Name" }
```

Response: `CategoryResponse`.

### DELETE /api/admin/categories/{id}

Fails `409` if category has recipes.

### GET /api/admin/users

Query: `?keyword=john&page=0&size=20&sort=createdAt`. Searches username/email.

Response: `Page<AdminUserResponse>`

```json
{ "id": 1, "username": "john123", "email": "john@example.com", "role": "USER", "enabled": true, "createdAt": "...", "updatedAt": null }
```

### GET /api/admin/users/{id}

Response: `AdminUserResponse`.

### PUT /api/admin/users/{id}

```json
{ "username": "newName", "email": "new@email.com" }
```

Both fields optional. Response: `AdminUserResponse`.

### PATCH /api/admin/users/{id}/enable

Response: void. Enables a disabled user.

### PATCH /api/admin/users/{id}/disable

Response: void. Cannot disable last admin (`400`).

### PATCH /api/admin/users/{id}/role

```json
{ "role": "ADMIN" }
```

Values: `"USER"` or `"ADMIN"`. Cannot remove own ADMIN role (`400`).

### GET /api/admin/comments

Query: `?keyword=search&page=0&size=20&sort=createdAt`. Searches username, recipe title, or content.

Response: `Page<CommentResponse>`.

### DELETE /api/admin/comments/{id}

Response: void.

### GET /api/admin/dashboard

Response: `DashboardResponse`

```json
{
  "totalRecipes": 50,
  "totalUsers": 100,
  "totalCategories": 8,
  "totalFavorites": 200,
  "totalRatings": 500,
  "totalComments": 300,
  "averageRating": 4.2,
  "newestUsers": [],
  "newestRecipes": []
}
```

---

## Cheat Sheet

```
PUBLIC (no auth)
  POST   /api/auth/register
  POST   /api/auth/login
  GET    /api/recipes                    ?keyword,page,size,sort
  GET    /api/recipes/{id}
  GET    /api/recipes/search             ?keyword,categoryId,ingredient,page,size,sort
  GET    /api/recipes/popular            ?page,size
  GET    /api/recipes/top-rated          ?page,size
  GET    /api/recipes/latest             ?page,size
  GET    /api/recipes/{id}/similar
  GET    /api/recipes/{id}/rating
  GET    /api/recipes/{id}/comments      ?page,size
  GET    /api/categories
  GET    /api/categories/{id}/recipes    ?page,size,sort

AUTH (any role)
  POST   /api/recipes/{id}/favorite
  DELETE /api/recipes/{id}/favorite
  POST   /api/recipes/{id}/rating
  POST   /api/recipes/{id}/comments
  PUT    /api/comments/{id}
  DELETE /api/comments/{id}
  GET    /api/users/me
  GET    /api/users/me/favorites         ?page,size

ADMIN (ROLE_ADMIN)
  GET    /api/admin/recipes              ?keyword,page,size,sort
  GET    /api/admin/recipes/{id}
  POST   /api/admin/recipes
  PUT    /api/admin/recipes/{id}
  DELETE /api/admin/recipes/{id}
  GET    /api/admin/categories
  GET    /api/admin/categories/{id}
  POST   /api/admin/categories
  PUT    /api/admin/categories/{id}
  DELETE /api/admin/categories/{id}
  GET    /api/admin/users                ?keyword,page,size,sort
  GET    /api/admin/users/{id}
  PUT    /api/admin/users/{id}
  PATCH  /api/admin/users/{id}/enable
  PATCH  /api/admin/users/{id}/disable
  PATCH  /api/admin/users/{id}/role
  GET    /api/admin/comments             ?keyword,page,size,sort
  DELETE /api/admin/comments/{id}
  GET    /api/admin/dashboard
```

---

## Important Behaviors

- **Duplicates**: favoriting twice returns `409`. Duplicate category name, username, or email also returns `409`.
- **Rating upsert**: `POST /api/recipes/{id}/rating` creates or updates (one rating per user per recipe).
- **Comment ownership**: only author can `PUT` (update). Admins can `DELETE` any.
- **Admin protection**: cannot disable last admin. Cannot remove own ADMIN role.
- **Category deletion**: fails `409` if category still has recipes.
- **Similar recipes**: max 10, based on shared category + common ingredients.
- **Current user rating**: `GET /api/recipes/{id}/rating` returns `currentUserRating: null` for anonymous users.
- **Nulls omitted**: Jackson config excludes null fields from JSON responses.
- **Dates**: ISO-8601 format, timezone `Asia/Ho_Chi_Minh`.
- **CORS**: any origin, methods GET/POST/PUT/DELETE/PATCH/OPTIONS, credentials allowed.
- **Swagger**: `http://localhost:8080/swagger-ui.html`, OpenAPI JSON at `/api-docs`.
