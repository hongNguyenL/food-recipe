import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { recipesApi } from '@/api/recipes'
import { RecipeCard, RecipeCardSkeleton } from '@/components/ui/recipe-card'
import { Pagination } from '@/components/ui/pagination'
import { ErrorComponent } from '@/components/ui/error-component'
import type { PopularRecipeResponse, Page } from '@/types'

function PopularityBadge({ score }: { score: number }) {
  const color =
    score >= 80 ? 'text-green-600' :
    score >= 50 ? 'text-yellow-600' :
    'text-orange-600'

  return (
    <span className={`inline-flex items-center gap-1 text-xs font-semibold ${color}`}>
      <span className="h-2 w-2 rounded-full bg-current" />
      {score.toFixed(0)}% popularity
    </span>
  )
}

export default function PopularPage() {
  const [page, setPage] = useState(0)

  const { data, isLoading, isError, refetch } = useQuery({
    queryKey: ['recipes', 'popular', { page }],
    queryFn: async () => {
      const res = await recipesApi.popular({ page, size: 20 })
      return res.data as Page<PopularRecipeResponse>
    },
  })

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Popular Recipes</h1>
        <p className="text-[var(--muted-foreground)]">Trending recipes loved by our community</p>
      </div>

      {isLoading ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {Array.from({ length: 8 }).map((_, i) => (
            <RecipeCardSkeleton key={i} />
          ))}
        </div>
      ) : isError ? (
        <ErrorComponent message="Failed to load popular recipes" onRetry={() => refetch()} />
      ) : data && data.content.length === 0 ? (
        <div className="text-center py-12 text-[var(--muted-foreground)]">
          No popular recipes yet.
        </div>
      ) : data ? (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {data.content.map((recipe: PopularRecipeResponse) => (
              <div key={recipe.id} className="relative">
                <RecipeCard recipe={recipe} />
                <div className="mt-1 flex items-center justify-between px-1">
                  <PopularityBadge score={recipe.popularityScore} />
                </div>
              </div>
            ))}
          </div>
          <Pagination
            page={data.page}
            totalPages={data.totalPages}
            onPageChange={setPage}
          />
        </>
      ) : null}
    </div>
  )
}
