import { useState } from 'react'
import { Link } from 'react-router-dom'
import { Card } from '@/components/ui/card'
import { RatingStars } from '@/components/ui/rating-stars'
import { FavoriteButton } from '@/components/ui/favorite-button'
import { Skeleton } from '@/components/ui/skeleton'
import type { RecipeSummary, SearchRecipeResponse, PopularRecipeResponse } from '@/types'

type RecipeCardData = (RecipeSummary | SearchRecipeResponse | PopularRecipeResponse) & { averageRating?: number; favoriteCount?: number }

interface RecipeCardProps {
  recipe: RecipeCardData
  isFavorited?: boolean
  onToggleFavorite?: () => void
  isFavoriteLoading?: boolean
}

export function RecipeCard({ recipe, isFavorited, onToggleFavorite, isFavoriteLoading }: RecipeCardProps) {
  const rating = 'averageRating' in recipe ? (recipe.averageRating ?? 0) : 0
  const favCount = 'favoriteCount' in recipe ? (recipe.favoriteCount ?? 0) : 0
  const [imgError, setImgError] = useState(false)

  return (
    <Link to={`/recipes/${recipe.id}`}>
      <Card className="group overflow-hidden transition-shadow hover:shadow-md">
        <div className="w-full overflow-hidden bg-[var(--muted)]" style={{ height: 200 }}>
          {recipe.imageUrl && !imgError ? (
            <img
              src={recipe.imageUrl}
              alt={recipe.title}
              className="h-full w-full object-cover transition-transform group-hover:scale-105"
              referrerPolicy="no-referrer"
              onError={() => setImgError(true)}
            />
          ) : (
            <div className="flex h-full items-center justify-center text-[var(--muted-foreground)] text-sm p-2 text-center">
              {recipe.title}
            </div>
          )}
        </div>
        <div className="p-4">
          <h3 className="font-semibold truncate">{recipe.title}</h3>
          <p className="text-sm text-[var(--muted-foreground)]">{recipe.categoryName}</p>
          <div className="mt-2 flex items-center justify-between">
            {rating > 0 && <RatingStars rating={rating} size={14} />}
            {onToggleFavorite && (
              <FavoriteButton
                isFavorited={isFavorited}
                count={favCount}
                onToggle={onToggleFavorite}
                isLoading={isFavoriteLoading}
              />
            )}
          </div>
        </div>
      </Card>
    </Link>
  )
}

export function RecipeCardSkeleton() {
  return (
    <Card className="overflow-hidden">
      <Skeleton className="aspect-video w-full rounded-none" />
      <div className="p-4 space-y-2">
        <Skeleton className="h-5 w-3/4" />
        <Skeleton className="h-4 w-1/2" />
        <div className="flex justify-between">
          <Skeleton className="h-4 w-24" />
          <Skeleton className="h-4 w-16" />
        </div>
      </div>
    </Card>
  )
}
