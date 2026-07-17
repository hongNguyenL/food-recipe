import type { ApiResponse, AuthResponse, LoginRequest, RegisterRequest, UserResponse } from '@/types'
import apiClient from './client'

export const authApi = {
  login: async (data: LoginRequest) => {
    const res = await apiClient.post<ApiResponse<AuthResponse>>('/api/auth/login', data)
    return res.data
  },
  register: async (data: RegisterRequest) => {
    const res = await apiClient.post<ApiResponse<UserResponse>>('/api/auth/register', data)
    return res.data
  },
}
