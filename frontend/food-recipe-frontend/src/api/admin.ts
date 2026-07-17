import type { AdminUserResponse, ApiResponse, CategoryResponse, CommentResponse, DashboardResponse, Page, RecipeDetail, RecipeSummary, RecipeFormData } from '@/types'
import apiClient from './client'

export const adminApi = {
  getDashboard: async () => {
    const res = await apiClient.get<ApiResponse<DashboardResponse>>('/api/admin/dashboard')
    return res.data
  },
  getRecipes: async (params?: { keyword?: string; page?: number; size?: number; sort?: string }) => {
    const res = await apiClient.get<ApiResponse<Page<RecipeSummary>>>('/api/admin/recipes', { params })
    return res.data
  },
  getRecipe: async (id: number) => {
    const res = await apiClient.get<ApiResponse<RecipeDetail>>(`/api/admin/recipes/${id}`)
    return res.data
  },
  createRecipe: async (data: RecipeFormData) => {
    const res = await apiClient.post<ApiResponse<RecipeDetail>>('/api/admin/recipes', data)
    return res.data
  },
  updateRecipe: async (id: number, data: RecipeFormData) => {
    const res = await apiClient.put<ApiResponse<RecipeDetail>>(`/api/admin/recipes/${id}`, data)
    return res.data
  },
  deleteRecipe: async (id: number) => {
    const res = await apiClient.delete<ApiResponse<void>>(`/api/admin/recipes/${id}`)
    return res.data
  },
  getCategories: async () => {
    const res = await apiClient.get<ApiResponse<CategoryResponse[]>>('/api/admin/categories')
    return res.data
  },
  getCategory: async (id: number) => {
    const res = await apiClient.get<ApiResponse<CategoryResponse>>(`/api/admin/categories/${id}`)
    return res.data
  },
  createCategory: async (name: string) => {
    const res = await apiClient.post<ApiResponse<CategoryResponse>>('/api/admin/categories', { name })
    return res.data
  },
  updateCategory: async (id: number, name: string) => {
    const res = await apiClient.put<ApiResponse<CategoryResponse>>(`/api/admin/categories/${id}`, { name })
    return res.data
  },
  deleteCategory: async (id: number) => {
    const res = await apiClient.delete<ApiResponse<void>>(`/api/admin/categories/${id}`)
    return res.data
  },
  getUsers: async (params?: { keyword?: string; page?: number; size?: number; sort?: string }) => {
    const res = await apiClient.get<ApiResponse<Page<AdminUserResponse>>>('/api/admin/users', { params })
    return res.data
  },
  getUser: async (id: number) => {
    const res = await apiClient.get<ApiResponse<AdminUserResponse>>(`/api/admin/users/${id}`)
    return res.data
  },
  updateUser: async (id: number, data: { username?: string; email?: string }) => {
    const res = await apiClient.put<ApiResponse<AdminUserResponse>>(`/api/admin/users/${id}`, data)
    return res.data
  },
  enableUser: async (id: number) => {
    const res = await apiClient.patch<ApiResponse<void>>(`/api/admin/users/${id}/enable`)
    return res.data
  },
  disableUser: async (id: number) => {
    const res = await apiClient.patch<ApiResponse<void>>(`/api/admin/users/${id}/disable`)
    return res.data
  },
  changeUserRole: async (id: number, role: 'USER' | 'ADMIN') => {
    const res = await apiClient.patch<ApiResponse<void>>(`/api/admin/users/${id}/role`, { role })
    return res.data
  },
  getComments: async (params?: { keyword?: string; page?: number; size?: number; sort?: string }) => {
    const res = await apiClient.get<ApiResponse<Page<CommentResponse>>>('/api/admin/comments', { params })
    return res.data
  },
  deleteComment: async (id: number) => {
    const res = await apiClient.delete<ApiResponse<void>>(`/api/admin/comments/${id}`)
    return res.data
  },
}
