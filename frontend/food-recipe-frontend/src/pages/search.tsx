import { useState, useEffect } from 'react'
import { useSearchParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { recipesApi } from '@/api/recipes'
import { categoriesApi } from '@/api/categories'
import { RecipeCard, RecipeCardSkeleton } from '@/components/ui/recipe-card'
import { SearchBar } from '@/components/ui/search-bar'
import { Pagination } from '@/components/ui/pagination'
import { Select } from '@/components/ui/select'
import { Input } from '@/components/ui/input'
import { ErrorComponent } from '@/components/ui/error-component'
import type { Page, SearchRecipeResponse } from '@/types'

const SORT_OPTIONS = [
  { value: 'title', label: 'Title' },
  { value: 'createdAt', label: 'Date' },
  { value: 'averageRating', label: 'Rating' },
  { value: 'favoriteCount', label: 'Favorites' },
  { value: 'commentCount', label: 'Comments' },
]

export default function SearchPage() {
  const [searchParams, setSearchParams] = useSearchParams()

  const keyword = searchParams.get('keyword') || ''
  const categoryId = searchParams.get('categoryId') || ''
  const ingredient = searchParams.get('ingredient') || ''
  const sort = searchParams.get('sort') || 'createdAt'
  const page = parseInt(searchParams.get('page') || '0', 10)

  const [keywordInput, setKeywordInput] = useState(keyword)
  const [ingredientInput, setIngredientInput] = useState(ingredient)

  useEffect(() => {
    setKeywordInput(keyword)
  }, [keyword])

  useEffect(() => {
    setIngredientInput(ingredient)
  }, [ingredient])

  const { data: categoriesData } = useQuery({
    queryKey: ['categories'],
    queryFn: () => categoriesApi.list(),
  })

  const { data, isLoading, isError, refetch } = useQuery({
    queryKey: ['recipes', 'search', { keyword, categoryId, ingredient, sort, page }],
    queryFn: async () => {
      const params: Record<string, string | number | undefined> = {
        page,
        size: 12,
        sort,
      }
      if (keyword) params.keyword = keyword
      if (categoryId) params.categoryId = Number(categoryId)
      if (ingredient) params.ingredient = ingredient
      const res = await recipesApi.search(params as any)
      return res.data as Page<SearchRecipeResponse>
    },
  })

  const updateParams = (updates: Record<string, string>) => {
    const newParams = new URLSearchParams(searchParams)
    Object.entries(updates).forEach(([key, value]) => {
      if (value) {
        newParams.set(key, value)
      } else {
        newParams.delete(key)
      }
    })
    if (!updates.page) {
      newParams.delete('page')
    }
    setSearchParams(newParams)
  }

  const handleSearch = () => {
    updateParams({ keyword: keywordInput, page: '' })
  }

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') handleSearch()
  }

  const categoryOptions = [
    { value: '', label: 'All Categories' },
    ...(categoriesData?.data?.map((c) => ({ value: String(c.id), label: c.name })) || []),
  ]

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Search Recipes</h1>
        <p className="text-[var(--muted-foreground)]">Find the perfect recipe</p>
      </div>

      <div className="flex flex-wrap gap-4 items-end">
        <div className="flex-1 min-w-[200px]">
          <SearchBar
            value={keywordInput}
            onChange={setKeywordInput}
            onClear={() => { setKeywordInput(''); updateParams({ keyword: '', page: '' }) }}
          />
        </div>
        <button
          onClick={handleSearch}
          className="h-10 px-4 rounded-md bg-[var(--primary)] text-[var(--primary-foreground)] text-sm font-medium hover:opacity-90"
        >
          Search
        </button>
      </div>

      <div className="flex flex-wrap gap-4" onKeyDown={handleKeyDown}>
        <div className="w-48">
          <Select
            label="Category"
            options={categoryOptions}
            value={categoryId}
            onChange={(e) => updateParams({ categoryId: e.target.value, page: '' })}
          />
        </div>
        <div className="w-48">
          <label className="text-sm font-medium block mb-1">Ingredient</label>
          <Input
            value={ingredientInput}
            onChange={(e) => setIngredientInput(e.target.value)}
            onBlur={() => updateParams({ ingredient: ingredientInput, page: '' })}
            placeholder="e.g. chicken"
          />
        </div>
        <div className="w-48">
          <Select
            label="Sort By"
            options={SORT_OPTIONS}
            value={sort}
            onChange={(e) => updateParams({ sort: e.target.value, page: '' })}
          />
        </div>
      </div>

      {isLoading ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {Array.from({ length: 8 }).map((_, i) => (
            <RecipeCardSkeleton key={i} />
          ))}
        </div>
      ) : isError ? (
        <ErrorComponent message="Failed to load search results" onRetry={() => refetch()} />
      ) : data && data.content.length === 0 ? (
        <div className="text-center py-12 text-[var(--muted-foreground)]">
          No recipes found. Try different search terms.
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
            onPageChange={(p) => updateParams({ page: String(p) })}
          />
        </>
      ) : null}
    </div>
  )
}
