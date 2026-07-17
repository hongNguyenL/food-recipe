import { useParams, Link } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { categoriesApi } from '@/api/categories'
import { RecipeCard, RecipeCardSkeleton } from '@/components/ui/recipe-card'
import { Pagination } from '@/components/ui/pagination'
import { ErrorComponent } from '@/components/ui/error-component'
import { ArrowLeft } from 'lucide-react'
import { useState } from 'react'

export default function CategoryRecipesPage() {
  const { id } = useParams<{ id: string }>()
  const [page, setPage] = useState(0)

  const categoryId = Number(id)

  const { data: categories } = useQuery({
    queryKey: ['categories'],
    queryFn: () => categoriesApi.list(),
  })

  const category = categories?.data?.find((c) => c.id === categoryId)

  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['category-recipes', categoryId, page],
    queryFn: () => categoriesApi.getRecipes(categoryId, { page, size: 20 }),
    enabled: !!categoryId,
  })

  const recipes = data?.data

  return (
    <div className="mx-auto max-w-7xl px-4 py-8">
      <Link
        to="/categories"
        className="mb-4 inline-flex items-center gap-1 text-sm text-[var(--muted-foreground)] hover:text-[var(--foreground)] transition-colors"
      >
        <ArrowLeft size={16} />
        Back to Categories
      </Link>

      <h1 className="text-3xl font-bold mb-8">
        {category?.name || 'Category Recipes'}
      </h1>

      {isLoading ? (
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
          {Array.from({ length: 8 }).map((_, i) => (
            <RecipeCardSkeleton key={i} />
          ))}
        </div>
      ) : error ? (
        <ErrorComponent message="Failed to load recipes" onRetry={() => refetch()} />
      ) : recipes && recipes.content.length > 0 ? (
        <>
          <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
            {recipes.content.map((recipe) => (
              <RecipeCard key={recipe.id} recipe={recipe} />
            ))}
          </div>
          <div className="mt-8">
            <Pagination
              page={recipes.number}
              totalPages={recipes.totalPages}
              onPageChange={setPage}
            />
          </div>
        </>
      ) : (
        <p className="text-[var(--muted-foreground)]">No recipes found in this category.</p>
      )}
    </div>
  )
}
