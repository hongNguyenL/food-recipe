import { useState, useEffect, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { recipesApi } from '@/api/recipes'
import { categoriesApi } from '@/api/categories'
import { useAuth } from '@/hooks/use-auth'
import { IngredientInput } from '@/components/ui/ingredient-input'
import { SuggestedIngredients } from '@/components/ui/suggested-ingredients'
import { PantryFilters } from '@/components/ui/pantry-filters'
import { PantryRecipeCard } from '@/components/ui/pantry-recipe-card'
import { Pagination } from '@/components/ui/pagination'
import { Button } from '@/components/ui/button'
import { EmptyPantryState, NoResultsState, SearchSkeleton, ErrorMessage } from '@/components/ui/pantry-states'
import { Search, LogIn } from 'lucide-react'

const STORAGE_KEY = 'pantry-ingredients'

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
  const [minMatch, setMinMatch] = useState(0)
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

  const searchQuery = useQuery({
    queryKey: ['pantry-search', { pantry, page, pageSize, minMatch, categoryId }],
    queryFn: () => recipesApi.pantrySearch({
      ingredients: pantry,
      page,
      size: pageSize,
      minMatchPercentage: minMatch > 0 ? minMatch : undefined,
      categoryId: categoryId ? Number(categoryId) : undefined,
    }),
    enabled: searched && pantry.length > 0 && isAuthenticated,
    retry: false,
  })

  const handleSearch = () => {
    if (pantry.length === 0) return
    setPage(0)
    setSearched(true)
  }

  const handleClear = () => {
    setPage(0)
    setMinMatch(0)
    setCategoryId('')
    setSearched(false)
  }

  const results = searchQuery.data?.data
  const hasSearched = searched && pantry.length > 0

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
        onMinMatchChange={(v) => { setMinMatch(v); setPage(0); if (hasSearched) setSearched(true) }}
        categoryId={categoryId}
        onCategoryChange={(v) => { setCategoryId(v); setPage(0); if (hasSearched) setSearched(true) }}
        pageSize={pageSize}
        onPageSizeChange={(v) => { setPageSize(v); setPage(0) }}
        categories={categories}
      />

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
          <NoResultsState onClear={handleClear} />
        ) : (
          <div className="space-y-4">
            <p className="text-sm text-[var(--muted-foreground)]">
              Found {results.totalElements} recipe{results.totalElements !== 1 ? 's' : ''}
            </p>
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
