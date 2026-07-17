import type { ApiResponse, CategoryResponse, Page, RecipeSummary } from '@/types'
import apiClient from './client'

export const categoriesApi = {
  list: async () => {
    const res = await apiClient.get<ApiResponse<CategoryResponse[]>>('/api/categories')
    return res.data
  },
  getRecipes: async (id: number, params?: { page?: number; size?: number; sort?: string }) => {
    const res = await apiClient.get<ApiResponse<Page<RecipeSummary>>>(`/api/categories/${id}/recipes`, { params })
    return res.data
  },
}
