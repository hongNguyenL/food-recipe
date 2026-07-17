export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
  errors: Record<string, string> | null
}

export interface Page<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  numberOfElements: number
  first: boolean
  last: boolean
  empty: boolean
}

export interface LoginRequest {
  usernameOrEmail: string
  password: string
}

export interface RegisterRequest {
  username: string
  email: string
  password: string
}

export interface AuthResponse {
  accessToken: string
  tokenType: string
  expiresIn: number
}

export interface UserResponse {
  id: number
  username: string
  email: string
  role: 'USER' | 'ADMIN'
}

export interface RecipeSummary {
  id: number
  title: string
  imageUrl: string
  categoryName: string
}

export interface RecipeDetail {
  id: number
  title: string
  imageUrl: string
  description: string
  category: { id: number; name: string }
  ingredients: { id: number; ingredientText: string }[]
  instructions: { id: number; stepNumber: number; instructionText: string }[]
  favoriteCount: number
  averageRating: number
  totalRatings: number
  totalComments: number
}

export interface SearchRecipeResponse {
  id: number
  title: string
  imageUrl: string
  categoryName: string
  createdAt: string
  averageRating: number
  favoriteCount: number
  commentCount: number
}

export interface PopularRecipeResponse {
  id: number
  title: string
  imageUrl: string
  categoryName: string
  averageRating: number
  favoriteCount: number
  commentCount: number
  popularityScore: number
}

export interface SimilarRecipeResponse {
  id: number
  title: string
  imageUrl: string
  categoryName: string
  averageRating: number
}

export interface CategoryResponse {
  id: number
  name: string
}

export interface CommentResponse {
  id: number
  recipeId: number
  userId: number
  username: string
  content: string
  createdAt: string
  updatedAt: string | null
}

export interface RatingResponse {
  id: number
  recipeId: number
  rating: number
}

export interface RecipeRatingResponse {
  averageRating: number
  totalRatings: number
  currentUserRating: number | null
}

export interface AdminUserResponse {
  id: number
  username: string
  email: string
  role: 'USER' | 'ADMIN'
  enabled: boolean
  createdAt: string
  updatedAt: string | null
}

export interface DashboardResponse {
  totalRecipes: number
  totalUsers: number
  totalCategories: number
  totalFavorites: number
  totalRatings: number
  totalComments: number
  averageRating: number
  newestUsers: { username: string; createdAt: string }[]
  newestRecipes: { title: string; createdAt: string }[]
}

export interface RecipeFormData {
  title: string
  imageUrl: string
  description: string
  categoryId: number
  ingredients: string[]
  instructions: { stepNumber: number; instructionText: string }[]
}
