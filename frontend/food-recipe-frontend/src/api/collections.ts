import type { ApiResponse, CollectionRequest, CollectionResponse, CollectionSummaryResponse, CollectionDetailResponse, CollectionRecipeResponse, Page } from '@/types'
import apiClient from './client'

export const collectionsApi = {
  create: async (data: CollectionRequest) => {
    const res = await apiClient.post<ApiResponse<CollectionResponse>>('/api/collections', data)
    return res.data
  },

  update: async (id: number, data: CollectionRequest) => {
    const res = await apiClient.put<ApiResponse<CollectionResponse>>(`/api/collections/${id}`, data)
    return res.data
  },

  delete: async (id: number) => {
    const res = await apiClient.delete<ApiResponse<void>>(`/api/collections/${id}`)
    return res.data
  },

  getMyCollections: async (params?: { page?: number; size?: number; sort?: string }) => {
    const res = await apiClient.get<ApiResponse<Page<CollectionSummaryResponse>>>('/api/users/me/collections', { params })
    return res.data
  },

  getPublicCollections: async (params?: { page?: number; size?: number; sort?: string }) => {
    const res = await apiClient.get<ApiResponse<Page<CollectionSummaryResponse>>>('/api/collections/public', { params })
    return res.data
  },

  searchCollections: async (params?: { keyword?: string; ownerUsername?: string; visibility?: string; page?: number; size?: number; sort?: string }) => {
    const res = await apiClient.get<ApiResponse<Page<CollectionSummaryResponse>>>('/api/collections/search', { params })
    return res.data
  },

  getById: async (id: number) => {
    const res = await apiClient.get<ApiResponse<CollectionDetailResponse>>(`/api/collections/${id}`)
    return res.data
  },

  addRecipe: async (collectionId: number, recipeId: number) => {
    const res = await apiClient.post<ApiResponse<CollectionRecipeResponse>>(`/api/collections/${collectionId}/recipes/${recipeId}`)
    return res.data
  },

  removeRecipe: async (collectionId: number, recipeId: number) => {
    const res = await apiClient.delete<ApiResponse<void>>(`/api/collections/${collectionId}/recipes/${recipeId}`)
    return res.data
  },

  getCollectionRecipes: async (collectionId: number, params?: { page?: number; size?: number; sort?: string }) => {
    const res = await apiClient.get<ApiResponse<Page<CollectionRecipeResponse>>>(`/api/collections/${collectionId}/recipes`, { params })
    return res.data
  },
}
