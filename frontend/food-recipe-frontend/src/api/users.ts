import type { ApiResponse, Page, RecipeSummary, UserResponse } from '@/types'
import apiClient from './client'

export const usersApi = {
  getMe: async () => {
    const res = await apiClient.get<ApiResponse<UserResponse>>('/api/users/me')
    return res.data
  },
  getFavorites: async (params?: { page?: number; size?: number }) => {
    const res = await apiClient.get<ApiResponse<Page<RecipeSummary>>>('/api/users/me/favorites', { params })
    return res.data
  },
}
