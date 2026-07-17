import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { recipesApi } from '@/api/recipes'
import { RecipeCard, RecipeCardSkeleton } from '@/components/ui/recipe-card'
import { Pagination } from '@/components/ui/pagination'
import { LoadingSpinner } from '@/components/ui/loading-spinner'
import { ErrorComponent } from '@/components/ui/error-component'
import type { Page, SearchRecipeResponse } from '@/types'

export default function TopRatedPage() {
  const [page, setPage] = useState(0)

  const { data, isLoading, isError, refetch } = useQuery({
    queryKey: ['recipes', 'top-rated', { page }],
    queryFn: async () => {
      const res = await recipesApi.topRated({ page, size: 20 })
      return res.data as Page<SearchRecipeResponse>
    },
  })

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Top Rated Recipes</h1>
        <p className="text-[var(--muted-foreground)]">The highest rated recipes from our community</p>
      </div>

      {isLoading ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {Array.from({ length: 8 }).map((_, i) => (
            <RecipeCardSkeleton key={i} />
          ))}
        </div>
      ) : isError ? (
        <ErrorComponent message="Failed to load top rated recipes" onRetry={() => refetch()} />
      ) : data && data.content.length === 0 ? (
        <div className="text-center py-12 text-[var(--muted-foreground)]">
          No top rated recipes yet.
        </div>
      ) : data ? (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {data.content.map((recipe) => (
              <RecipeCard key={recipe.id} recipe={recipe} />
            ))}
          </div>
          <Pagination
            page={data.number}
            totalPages={data.totalPages}
            onPageChange={setPage}
          />
        </>
      ) : null}
    </div>
  )
}
