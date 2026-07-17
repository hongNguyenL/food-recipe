import { createContext, useContext, useState, useEffect, useCallback, type ReactNode } from 'react'
import { authApi } from '@/api/auth'
import { usersApi } from '@/api/users'
import type { ApiResponse, AuthResponse, LoginRequest, RegisterRequest, UserResponse } from '@/types'

interface AuthContextType {
  user: UserResponse | null
  isLoading: boolean
  login: (data: LoginRequest) => Promise<ApiResponse<AuthResponse>>
  register: (data: RegisterRequest) => Promise<ApiResponse<UserResponse>>
  logout: () => void
  isAuthenticated: boolean
  isAdmin: boolean
  setUserFromToken: () => Promise<void>
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserResponse | null>(() => {
    const stored = localStorage.getItem('user')
    return stored ? JSON.parse(stored) : null
  })
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    setIsLoading(false)
  }, [])

  const login = useCallback(async (data: LoginRequest) => {
    const res = await authApi.login(data)
    localStorage.setItem('accessToken', res.data.accessToken)
    return res
  }, [])

  const register = useCallback(async (data: RegisterRequest) => {
    const res = await authApi.register(data)
    return res
  }, [])

  const setUserFromToken = useCallback(async () => {
    try {
      const res = await usersApi.getMe()
      setUser(res.data)
      localStorage.setItem('user', JSON.stringify(res.data))
    } catch {
      localStorage.removeItem('accessToken')
      localStorage.removeItem('user')
    }
  }, [])

  const logout = useCallback(() => {
    setUser(null)
    localStorage.removeItem('accessToken')
    localStorage.removeItem('user')
  }, [])

  return (
    <AuthContext.Provider
      value={{
        user,
        isLoading,
        login,
        register,
        logout,
        setUserFromToken,
        isAuthenticated: !!user && !!localStorage.getItem('accessToken'),
        isAdmin: user?.role === 'ADMIN',
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}
