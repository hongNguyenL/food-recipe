import type { ApiResponse, Page, RecipeDetail, RecipeSummary, SearchRecipeResponse, PopularRecipeResponse, SimilarRecipeResponse, RecipeRatingResponse, RatingResponse, CommentResponse, RecipeFormData, PantrySearchRequest, PantrySearchResult } from '@/types'
import apiClient from './client'

export const recipesApi = {
  list: async (params?: { keyword?: string; page?: number; size?: number; sort?: string }) => {
    const res = await apiClient.get<ApiResponse<Page<RecipeSummary>>>('/api/recipes', { params })
    return res.data
  },
  getById: async (id: number) => {
    const res = await apiClient.get<ApiResponse<RecipeDetail>>(`/api/recipes/${id}`)
    return res.data
  },
  search: async (params?: { keyword?: string; categoryId?: number; ingredient?: string; page?: number; size?: number; sort?: string }) => {
    const res = await apiClient.get<ApiResponse<Page<SearchRecipeResponse>>>('/api/recipes/search', { params })
    return res.data
  },
  popular: async (params?: { page?: number; size?: number }) => {
    const res = await apiClient.get<ApiResponse<Page<PopularRecipeResponse>>>('/api/recipes/popular', { params })
    return res.data
  },
  topRated: async (params?: { page?: number; size?: number }) => {
    const res = await apiClient.get<ApiResponse<Page<SearchRecipeResponse>>>('/api/recipes/top-rated', { params })
    return res.data
  },
  latest: async (params?: { page?: number; size?: number }) => {
    const res = await apiClient.get<ApiResponse<Page<RecipeSummary>>>('/api/recipes/latest', { params })
    return res.data
  },
  similar: async (id: number) => {
    const res = await apiClient.get<ApiResponse<SimilarRecipeResponse[]>>(`/api/recipes/${id}/similar`)
    return res.data
  },
  favorite: async (id: number) => {
    const res = await apiClient.post<ApiResponse<void>>(`/api/recipes/${id}/favorite`)
    return res.data
  },
  unfavorite: async (id: number) => {
    const res = await apiClient.delete<ApiResponse<void>>(`/api/recipes/${id}/favorite`)
    return res.data
  },
  getRating: async (id: number) => {
    const res = await apiClient.get<ApiResponse<RecipeRatingResponse>>(`/api/recipes/${id}/rating`)
    return res.data
  },
  rate: async (id: number, rating: number) => {
    const res = await apiClient.post<ApiResponse<RatingResponse>>(`/api/recipes/${id}/rating`, { rating })
    return res.data
  },
  getComments: async (id: number, params?: { page?: number; size?: number }) => {
    const res = await apiClient.get<ApiResponse<Page<CommentResponse>>>(`/api/recipes/${id}/comments`, { params })
    return res.data
  },
  addComment: async (id: number, content: string) => {
    const res = await apiClient.post<ApiResponse<CommentResponse>>(`/api/recipes/${id}/comments`, { content })
    return res.data
  },
  pantrySearch: async (data: PantrySearchRequest) => {
    const res = await apiClient.post<ApiResponse<Page<PantrySearchResult>>>('/api/recipes/pantry-search', data)
    return res.data
  },
}
