import type { ApiResponse, CommentResponse } from '@/types'
import apiClient from './client'

export const commentsApi = {
  update: async (id: number, content: string) => {
    const res = await apiClient.put<ApiResponse<CommentResponse>>(`/api/comments/${id}`, { content })
    return res.data
  },
  delete: async (id: number) => {
    const res = await apiClient.delete<ApiResponse<void>>(`/api/comments/${id}`)
    return res.data
  },
}
