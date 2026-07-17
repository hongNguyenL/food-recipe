import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { useAuth } from '@/hooks/use-auth'
import { usersApi } from '@/api/users'
import { RecipeCard, RecipeCardSkeleton } from '@/components/ui/recipe-card'
import { Pagination } from '@/components/ui/pagination'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { ErrorComponent } from '@/components/ui/error-component'
import type { Page, RecipeSummary } from '@/types'
import { User, Mail, Shield } from 'lucide-react'

export default function DashboardPage() {
  const { user } = useAuth()
  const [page, setPage] = useState(0)

  const { data, isLoading, isError, refetch } = useQuery({
    queryKey: ['user', 'favorites', { page }],
    queryFn: async () => {
      const res = await usersApi.getFavorites({ page, size: 20 })
      return res.data as Page<RecipeSummary>
    },
  })

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">My Dashboard</h1>
        <p className="text-[var(--muted-foreground)]">Manage your profile and favorites</p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Profile Info</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex flex-col gap-3">
            <div className="flex items-center gap-2 text-sm">
              <User size={16} className="text-[var(--muted-foreground)]" />
              <span className="font-medium">{user?.username}</span>
            </div>
            <div className="flex items-center gap-2 text-sm">
              <Mail size={16} className="text-[var(--muted-foreground)]" />
              <span>{user?.email}</span>
            </div>
            <div className="flex items-center gap-2 text-sm">
              <Shield size={16} className="text-[var(--muted-foreground)]" />
              <Badge variant={user?.role === 'ADMIN' ? 'default' : 'secondary'}>
                {user?.role}
              </Badge>
            </div>
          </div>
        </CardContent>
      </Card>

      <div>
        <h2 className="text-xl font-semibold mb-4">My Favorites</h2>
        {isLoading ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {Array.from({ length: 8 }).map((_, i) => (
              <RecipeCardSkeleton key={i} />
            ))}
          </div>
        ) : isError ? (
          <ErrorComponent message="Failed to load favorites" onRetry={() => refetch()} />
        ) : data && data.content.length === 0 ? (
          <div className="text-center py-12 text-[var(--muted-foreground)]">
            No favorite recipes yet.
          </div>
        ) : data ? (
          <>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
              {data.content.map((recipe) => (
                <RecipeCard key={recipe.id} recipe={recipe} />
              ))}
            </div>
            <div className="mt-6">
              <Pagination
                page={data.number}
                totalPages={data.totalPages}
                onPageChange={setPage}
              />
            </div>
          </>
        ) : null}
      </div>
    </div>
  )
}
