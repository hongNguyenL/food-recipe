import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { recipesApi } from '@/api/recipes'
import { RecipeCard, RecipeCardSkeleton } from '@/components/ui/recipe-card'
import { SearchBar } from '@/components/ui/search-bar'
import { Button } from '@/components/ui/button'
import { ErrorComponent } from '@/components/ui/error-component'
import { ArrowRight } from 'lucide-react'

function RecipeSection({ title, linkTo, children }: { title: string; linkTo: string; children: React.ReactNode }) {
  return (
    <section>
      <div className="mb-6 flex items-center justify-between">
        <h2 className="text-2xl font-bold">{title}</h2>
        <Link
          to={linkTo}
          className="inline-flex items-center gap-1 text-sm text-[var(--primary)] hover:underline"
        >
          View All <ArrowRight size={14} />
        </Link>
      </div>
      <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
        {children}
      </div>
    </section>
  )
}

export default function Home() {
  const navigate = useNavigate()
  const [keyword, setKeyword] = useState('')

  const handleSearch = () => {
    if (keyword.trim()) {
      navigate(`/recipes?keyword=${encodeURIComponent(keyword.trim())}`)
    }
  }

  const topRatedQuery = useQuery({
    queryKey: ['recipes', 'top-rated', 4],
    queryFn: () => recipesApi.topRated({ size: 4 }),
  })

  const popularQuery = useQuery({
    queryKey: ['recipes', 'popular', 4],
    queryFn: () => recipesApi.popular({ size: 4 }),
  })

  const latestQuery = useQuery({
    queryKey: ['recipes', 'latest', 4],
    queryFn: () => recipesApi.latest({ size: 4 }),
  })

  return (
    <div className="space-y-12">
      <section className="flex flex-col items-center gap-6 py-16 text-center">
        <h1 className="text-4xl font-bold tracking-tight sm:text-5xl">
          Discover Delicious Recipes
        </h1>
        <p className="max-w-2xl text-lg text-[var(--muted-foreground)]">
          Find and share your favorite recipes from around the world
        </p>
        <div className="flex w-full max-w-md gap-2">
          <SearchBar
            value={keyword}
            onChange={setKeyword}
            placeholder="Search recipes..."
          />
          <Button onClick={handleSearch}>
            Search
          </Button>
        </div>
      </section>

      <RecipeSection title="Top Rated" linkTo="/top-rated">
        {topRatedQuery.isPending
          ? Array.from({ length: 4 }).map((_, i) => <RecipeCardSkeleton key={i} />)
          : topRatedQuery.isError
            ? <ErrorComponent message="Failed to load top rated recipes" onRetry={() => topRatedQuery.refetch()} />
            : topRatedQuery.data.data.content.map((recipe) => (
                <RecipeCard key={recipe.id} recipe={recipe} />
              ))
        }
      </RecipeSection>

      <RecipeSection title="Popular" linkTo="/popular">
        {popularQuery.isPending
          ? Array.from({ length: 4 }).map((_, i) => <RecipeCardSkeleton key={i} />)
          : popularQuery.isError
            ? <ErrorComponent message="Failed to load popular recipes" onRetry={() => popularQuery.refetch()} />
            : popularQuery.data.data.content.map((recipe) => (
                <RecipeCard key={recipe.id} recipe={recipe} />
              ))
        }
      </RecipeSection>

      <RecipeSection title="Latest" linkTo="/latest">
        {latestQuery.isPending
          ? Array.from({ length: 4 }).map((_, i) => <RecipeCardSkeleton key={i} />)
          : latestQuery.isError
            ? <ErrorComponent message="Failed to load latest recipes" onRetry={() => latestQuery.refetch()} />
            : latestQuery.data.data.content.map((recipe) => (
                <RecipeCard key={recipe.id} recipe={recipe} />
              ))
        }
      </RecipeSection>
    </div>
  )
}
