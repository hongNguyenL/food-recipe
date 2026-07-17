import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { useAuth } from '@/hooks/use-auth'
import { usersApi } from '@/api/users'
import { collectionsApi } from '@/api/collections'
import { RecipeCard, RecipeCardSkeleton } from '@/components/ui/recipe-card'
import { CollectionCard } from '@/components/ui/collection-card'
import { Pagination } from '@/components/ui/pagination'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { ErrorComponent } from '@/components/ui/error-component'
import type { Page, RecipeSummary } from '@/types'
import { User, Mail, Shield, BookOpen } from 'lucide-react'

type Tab = 'favorites' | 'collections'

export default function DashboardPage() {
  const { user } = useAuth()
  const [tab, setTab] = useState<Tab>('favorites')
  const [favPage, setFavPage] = useState(0)
  const [colPage, setColPage] = useState(0)

  const favQuery = useQuery({
    queryKey: ['user', 'favorites', { page: favPage }],
    queryFn: async () => {
      const res = await usersApi.getFavorites({ page: favPage, size: 20 })
      return res.data as Page<RecipeSummary>
    },
    enabled: tab === 'favorites',
  })

  const colQuery = useQuery({
    queryKey: ['my-collections', colPage],
    queryFn: () => collectionsApi.getMyCollections({ page: colPage, size: 20 }),
    enabled: tab === 'collections',
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

      <div className="flex gap-4 border-b border-[var(--border)]">
        <button
          onClick={() => setTab('favorites')}
          className={`pb-2 text-sm font-medium border-b-2 transition-colors ${
            tab === 'favorites' ? 'border-[var(--primary)] text-[var(--primary)]' : 'border-transparent text-[var(--muted-foreground)] hover:text-[var(--foreground)]'
          }`}
        >
          My Favorites
        </button>
        <button
          onClick={() => setTab('collections')}
          className={`pb-2 text-sm font-medium border-b-2 transition-colors ${
            tab === 'collections' ? 'border-[var(--primary)] text-[var(--primary)]' : 'border-transparent text-[var(--muted-foreground)] hover:text-[var(--foreground)]'
          }`}
        >
          My Collections
        </button>
      </div>

      {tab === 'favorites' && (
        <div>
          <h2 className="text-xl font-semibold mb-4">My Favorites</h2>
          {favQuery.isLoading ? (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
              {Array.from({ length: 8 }).map((_, i) => (
                <RecipeCardSkeleton key={i} />
              ))}
            </div>
          ) : favQuery.isError ? (
            <ErrorComponent message="Failed to load favorites" onRetry={() => favQuery.refetch()} />
          ) : favQuery.data && favQuery.data.content.length === 0 ? (
            <div className="text-center py-12 text-[var(--muted-foreground)]">
              No favorite recipes yet.
            </div>
          ) : favQuery.data ? (
            <>
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                {favQuery.data.content.map((recipe) => (
                  <RecipeCard key={recipe.id} recipe={recipe} />
                ))}
              </div>
              <div className="mt-6">
                <Pagination
                  page={favQuery.data.number}
                  totalPages={favQuery.data.totalPages}
                  onPageChange={setFavPage}
                />
              </div>
            </>
          ) : null}
        </div>
      )}

      {tab === 'collections' && (
        <div>
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-xl font-semibold">My Collections</h2>
            <Link to="/my-collections/new">
              <Button size="sm">New Collection</Button>
            </Link>
          </div>
          {colQuery.isLoading ? (
            <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
              {Array.from({ length: 4 }).map((_, i) => (
                <div key={i} className="h-32 rounded-lg bg-[var(--muted)] animate-pulse" />
              ))}
            </div>
          ) : colQuery.isError ? (
            <ErrorComponent message="Failed to load collections" onRetry={() => colQuery.refetch()} />
          ) : colQuery.data && colQuery.data.data.content.length === 0 ? (
            <div className="text-center py-12 text-[var(--muted-foreground)]">
              <BookOpen size={48} className="mx-auto mb-4 text-[var(--muted-foreground)]" />
              <p className="text-lg font-medium">No collections yet</p>
              <Link to="/my-collections/new" className="mt-2 inline-block text-sm text-[var(--primary)] hover:underline">
                Create your first collection
              </Link>
            </div>
          ) : colQuery.data ? (
            <>
              <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
                {colQuery.data.data.content.map((col) => (
                  <CollectionCard key={col.id} collection={col} />
                ))}
              </div>
              <div className="mt-6">
                <Pagination
                  page={colQuery.data.data.number}
                  totalPages={colQuery.data.data.totalPages}
                  onPageChange={setColPage}
                />
              </div>
            </>
          ) : null}
        </div>
      )}
    </div>
  )
}
