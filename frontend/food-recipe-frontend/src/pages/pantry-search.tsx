import { useState, useEffect, useCallback } from 'react'
import { Search, Refrigerator, AlertCircle, PackageOpen } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Select } from '@/components/ui/select'
import { Pagination } from '@/components/ui/pagination'
import { IngredientInput } from '@/components/ui/pantry/IngredientInput'
import { PantryResultCard, PantryResultCardSkeleton } from '@/components/ui/pantry/PantryResultCard'
import { recipesApi } from '@/api/recipes'
import { categoriesApi } from '@/api/categories'
import { usersApi } from '@/api/users'
import { useAuth } from '@/hooks/use-auth'
import type { PantrySearchResult, CategoryResponse } from '@/types'
import toast from 'react-hot-toast'

export default function PantrySearchPage() {
  const { isAuthenticated } = useAuth()
  const [ingredients, setIngredients] = useState<string[]>([])
  const [categories, setCategories] = useState<CategoryResponse[]>([])
  const [selectedCategory, setSelectedCategory] = useState<number | undefined>(undefined)
  const [minMatch, setMinMatch] = useState<number | undefined>(undefined)
  const [results, setResults] = useState<PantrySearchResult[]>([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [loading, setLoading] = useState(false)
  const [searched, setSearched] = useState(false)
  const [favorites, setFavorites] = useState<Set<number>>(new Set())
  const [favoriteLoading, setFavoriteLoading] = useState<Set<number>>(new Set())

  useEffect(() => {
    categoriesApi.list().then(res => {
      if (res.success) setCategories(res.data ?? [])
    }).catch(() => {})
  }, [])

  useEffect(() => {
    if (!isAuthenticated) return
    usersApi.getFavorites({ page: 0, size: 1000 }).then(res => {
      if (res.success && res.data) {
        setFavorites(new Set(res.data.content.map(r => r.id)))
      }
    }).catch(() => {})
  }, [isAuthenticated])

  const doSearch = useCallback(async (pageNum: number) => {
    if (ingredients.length === 0) {
      toast.error('Please add at least one ingredient')
      return
    }
    setLoading(true)
    setSearched(true)
    try {
      const res = await recipesApi.pantrySearch({
        ingredients,
        page: pageNum,
        size: 12,
        minMatchPercentage: minMatch,
        categoryId: selectedCategory,
      })
      if (res.success) {
        setResults(res.data.content)
        setTotalPages(res.data.totalPages)
        setTotalElements(res.data.totalElements)
        setPage(res.data.number)
      }
    } catch {
      toast.error('Search failed. Please try again.')
    } finally {
      setLoading(false)
    }
  }, [ingredients, minMatch, selectedCategory])

  const handleToggleFavorite = async (recipeId: number) => {
    if (!isAuthenticated) {
      toast.error('Please login to favorite recipes')
      return
    }
    setFavoriteLoading(prev => new Set(prev).add(recipeId))
    try {
      if (favorites.has(recipeId)) {
        await recipesApi.unfavorite(recipeId)
        setFavorites(prev => { const n = new Set(prev); n.delete(recipeId); return n })
        toast.success('Removed from favorites')
      } else {
        await recipesApi.favorite(recipeId)
        setFavorites(prev => new Set(prev).add(recipeId))
        toast.success('Added to favorites')
      }
    } catch {
      toast.error('Failed to update favorite')
    } finally {
      setFavoriteLoading(prev => { const n = new Set(prev); n.delete(recipeId); return n })
    }
  }

  return (
    <div className="mx-auto max-w-7xl px-4 py-8 space-y-8">
      <div className="text-center space-y-2">
        <div className="flex items-center justify-center gap-2 text-[var(--primary)]">
          <Refrigerator size={32} />
          <h1 className="text-3xl font-bold">What's in My Fridge?</h1>
        </div>
        <p className="text-[var(--muted-foreground)] max-w-xl mx-auto">
          Enter the ingredients you have on hand, and we'll find recipes you can make.
        </p>
      </div>

      <div className="mx-auto max-w-2xl space-y-4 rounded-lg border border-[var(--border)] p-6">
        <IngredientInput
          ingredients={ingredients}
          onAdd={(ing) => setIngredients(prev => [...prev, ing])}
          onRemove={(i) => setIngredients(prev => prev.filter((_, idx) => idx !== i))}
        />

        <div className="flex flex-wrap gap-4">
          <div className="flex-1 min-w-[200px]">
            <Select
              label="Minimum Match"
              value={minMatch?.toString() ?? ''}
              onChange={(e) => setMinMatch(e.target.value ? Number(e.target.value) : undefined)}
              options={[
                { value: '', label: 'Any match' },
                { value: '25', label: '25%+' },
                { value: '50', label: '50%+' },
                { value: '75', label: '75%+' },
                { value: '90', label: '90%+' },
              ]}
            />
          </div>
          <div className="flex-1 min-w-[200px]">
            <Select
              label="Category"
              value={selectedCategory?.toString() ?? ''}
              onChange={(e) => setSelectedCategory(e.target.value ? Number(e.target.value) : undefined)}
              options={[
                { value: '', label: 'All categories' },
                ...categories.map(cat => ({ value: cat.id.toString(), label: cat.name })),
              ]}
            />
          </div>
        </div>

        <Button
          onClick={() => doSearch(0)}
          disabled={ingredients.length === 0 || loading}
          className="w-full"
          size="lg"
        >
          {loading ? (
            <span className="flex items-center gap-2">
              <span className="h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent" />
              Searching...
            </span>
          ) : (
            <span className="flex items-center gap-2">
              <Search size={18} />
              Search Recipes
            </span>
          )}
        </Button>
      </div>

      {searched && !loading && results.length === 0 && (
        <div className="flex flex-col items-center gap-3 py-16 text-center">
          <PackageOpen size={48} className="text-[var(--muted-foreground)]" />
          <h2 className="text-xl font-semibold">No recipes found</h2>
          <p className="text-[var(--muted-foreground)] max-w-md">
            Try adding more ingredients, lowering the minimum match percentage, or changing the category filter.
          </p>
        </div>
      )}

      {loading && (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {Array.from({ length: 6 }).map((_, i) => (
            <PantryResultCardSkeleton key={i} />
          ))}
        </div>
      )}

      {!loading && results.length > 0 && (
        <>
          <p className="text-sm text-[var(--muted-foreground)]">
            Found {totalElements} recipe{totalElements !== 1 ? 's' : ''}
          </p>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {results.map(r => (
              <PantryResultCard
                key={r.recipeId}
                result={r}
                isFavorited={favorites.has(r.recipeId)}
                onToggleFavorite={() => handleToggleFavorite(r.recipeId)}
                isFavoriteLoading={favoriteLoading.has(r.recipeId)}
              />
            ))}
          </div>

          {totalPages > 1 && (
            <Pagination
              page={page}
              totalPages={totalPages}
              onPageChange={(p) => doSearch(p)}
            />
          )}
        </>
      )}

      {!searched && !loading && (
        <div className="flex flex-col items-center gap-3 py-16 text-center">
          <AlertCircle size={48} className="text-[var(--muted-foreground)]" />
          <h2 className="text-xl font-semibold">Ready to cook?</h2>
          <p className="text-[var(--muted-foreground)] max-w-md">
            Type ingredients you have into the box above and click Search to find matching recipes.
          </p>
        </div>
      )}
    </div>
  )
}
