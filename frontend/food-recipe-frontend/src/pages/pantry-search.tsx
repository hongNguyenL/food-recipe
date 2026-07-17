import { useState, useEffect, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { recipesApi } from '@/api/recipes'
import { categoriesApi } from '@/api/categories'
import { useAuth } from '@/hooks/use-auth'
import { IngredientInput } from '@/components/ui/ingredient-input'
import { SuggestedIngredients } from '@/components/ui/suggested-ingredients'
import { PantryFilters } from '@/components/ui/pantry-filters'
import { FilterSummary } from '@/components/ui/filter-summary'
import { PantryRecipeCard } from '@/components/ui/pantry-recipe-card'
import { Pagination } from '@/components/ui/pagination'
import { Button } from '@/components/ui/button'
import { EmptyPantryState, SearchSkeleton, ErrorMessage } from '@/components/ui/pantry-states'
import { NoResultsSuggestion } from '@/components/ui/no-results-suggestion'
import { Search, LogIn } from 'lucide-react'
import toast from 'react-hot-toast'

const STORAGE_KEY = 'pantry-ingredients'
const DEFAULT_MIN_MATCH = 70

export default function PantrySearchPage() {
  const navigate = useNavigate()
  const { isAuthenticated } = useAuth()
  const [pantry, setPantry] = useState<string[]>(() => {
    try {
      const stored = localStorage.getItem(STORAGE_KEY)
      return stored ? JSON.parse(stored) : []
    } catch { return [] }
  })
  const [page, setPage] = useState(0)
  const [pageSize, setPageSize] = useState(20)
  const [minMatch, setMinMatch] = useState(DEFAULT_MIN_MATCH)
  const [categoryId, setCategoryId] = useState('')
  const [searched, setSearched] = useState(false)

  useEffect(() => {
    if (isAuthenticated && pantry.length > 0) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(pantry))
    }
  }, [isAuthenticated, pantry])

  const addIngredient = useCallback((name: string) => {
    const trimmed = name.trim().toLowerCase()
    if (!trimmed) return
    setPantry((prev) => {
      if (prev.length >= 30) return prev
      if (prev.some((i) => i.toLowerCase() === trimmed)) return prev
      return [...prev, trimmed]
    })
  }, [])

  const removeIngredient = useCallback((name: string) => {
    setPantry((prev) => prev.filter((i) => i !== name))
  }, [])

  const { data: catData } = useQuery({
    queryKey: ['categories'],
    queryFn: () => categoriesApi.list(),
  })
  const categories = catData?.data || []
  const selectedCategoryName = categoryId
    ? categories.find((c) => String(c.id) === categoryId)?.name ?? null
    : null

  const searchQuery = useQuery({
    queryKey: ['pantry-search', { pantry, page, pageSize, minMatch, categoryId }],
    queryFn: () => recipesApi.pantrySearch({
      ingredients: pantry,
      page,
      size: pageSize,
      minMatchPercentage: minMatch,
      categoryId: categoryId ? Number(categoryId) : undefined,
    }),
    enabled: searched && pantry.length > 0 && isAuthenticated,
    retry: false,
  })

  const handleSearch = () => {
    if (pantry.length === 0) {
      toast.error('Add at least one ingredient to search')
      return
    }
    setPage(0)
    setSearched(true)
  }

  const handleClear = () => {
    setPage(0)
    setMinMatch(DEFAULT_MIN_MATCH)
    setCategoryId('')
    setSearched(false)
  }

  const handleLowerThreshold = (value: number) => {
    setMinMatch(value)
    if (pantry.length > 0 && isAuthenticated) {
      setPage(0)
      setSearched(true)
    }
  }

  const results = searchQuery.data?.data
  const hasSearched = searched && pantry.length > 0
  const totalElements = results?.totalElements ?? 0

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">What's in My Fridge?</h1>
        <p className="text-[var(--muted-foreground)]">
          Enter the ingredients you already have and discover recipes you can make.
        </p>
      </div>

      <div className="space-y-4 rounded-lg border border-[var(--border)] p-4">
        <IngredientInput
          ingredients={pantry}
          onAdd={addIngredient}
          onRemove={removeIngredient}
        />
        <SuggestedIngredients pantry={pantry} onAdd={addIngredient} />

        <Button
          onClick={handleSearch}
          disabled={pantry.length === 0}
          className="w-full sm:w-auto"
        >
          <Search size={16} />
          Search Recipes
        </Button>
      </div>

      <PantryFilters
        minMatch={minMatch}
        onMinMatchChange={(v) => { setMinMatch(v); setPage(0) }}
        categoryId={categoryId}
        onCategoryChange={(v) => { setCategoryId(v); setPage(0) }}
        pageSize={pageSize}
        onPageSizeChange={(v) => { setPageSize(v); setPage(0) }}
        categories={categories}
      />

      {hasSearched && (
        <FilterSummary
          ingredients={pantry}
          minMatch={minMatch}
          categoryName={selectedCategoryName}
          onClear={handleClear}
        />
      )}

      {!hasSearched && !searchQuery.isLoading && !isAuthenticated && (
        <div className="flex flex-col items-center justify-center py-20 text-center">
          <LogIn size={48} className="text-[var(--muted-foreground)] mb-4" />
          <h2 className="text-xl font-semibold">Log in to use Pantry Search</h2>
          <p className="mt-2 text-[var(--muted-foreground)]">You need an account to search recipes by ingredients.</p>
          <Button className="mt-6" onClick={() => navigate('/login')}>Log In</Button>
        </div>
      )}

      {!hasSearched && !searchQuery.isLoading && isAuthenticated && <EmptyPantryState />}

      {hasSearched && searchQuery.isLoading && <SearchSkeleton />}

      {hasSearched && searchQuery.isError && (
        <ErrorMessage onRetry={() => searchQuery.refetch()} />
      )}

      {hasSearched && !searchQuery.isLoading && !searchQuery.isError && results && (
        results.content.length === 0 ? (
          <NoResultsSuggestion
            minMatch={minMatch}
            onLowerThreshold={handleLowerThreshold}
            onClear={handleClear}
          />
        ) : (
          <div className="space-y-4">
            <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-1">
              <p className="text-sm text-[var(--muted-foreground)]">
                Found {totalElements} recipe{totalElements !== 1 ? 's' : ''}
                {totalElements > 1000 && (
                  <span className="ml-2 text-xs text-[var(--primary)]">
                    Showing the best matching recipes first.
                  </span>
                )}
              </p>
              {totalElements < 20 && totalElements > 0 && (
                <p className="text-xs text-[var(--muted-foreground)]">
                  Try lowering the minimum match percentage to discover more recipes.
                </p>
              )}
            </div>
            <div className="space-y-4">
              {results.content.map((r) => (
                <PantryRecipeCard key={r.recipeId} result={r} />
              ))}
            </div>
            {results.totalPages > 1 && (
              <Pagination page={results.number} totalPages={results.totalPages} onPageChange={setPage} />
            )}
          </div>
        )
      )}
    </div>
  )
}
