import { useState } from 'react'
import { Link } from 'react-router-dom'
import { Card } from '@/components/ui/card'
import { RatingStars } from '@/components/ui/rating-stars'
import { FavoriteButton } from '@/components/ui/favorite-button'
import { MatchBadge } from './MatchBadge'
import { MatchedIngredientList } from './MatchedIngredientList'
import { MissingIngredientList } from './MissingIngredientList'
import { Skeleton } from '@/components/ui/skeleton'
import type { PantrySearchResult } from '@/types'

interface PantryResultCardProps {
  result: PantrySearchResult
  isFavorited?: boolean
  onToggleFavorite?: () => void
  isFavoriteLoading?: boolean
}

export function PantryResultCard({ result, isFavorited, onToggleFavorite, isFavoriteLoading }: PantryResultCardProps) {
  const [imgError, setImgError] = useState(false)

  return (
    <Card className="group overflow-hidden transition-shadow hover:shadow-md">
      <Link to={`/recipes/${result.recipeId}`}>
        <div className="w-full overflow-hidden bg-[var(--muted)]" style={{ height: 180 }}>
          {result.imageUrl && !imgError ? (
            <img
              src={result.imageUrl}
              alt={result.title}
              className="h-full w-full object-cover transition-transform group-hover:scale-105"
              referrerPolicy="no-referrer"
              onError={() => setImgError(true)}
            />
          ) : (
            <div className="flex h-full items-center justify-center text-[var(--muted-foreground)] text-sm p-2 text-center">
              {result.title}
            </div>
          )}
        </div>
      </Link>
      <div className="p-4 space-y-3">
        <div className="flex items-start justify-between gap-2">
          <Link to={`/recipes/${result.recipeId}`} className="min-w-0">
            <h3 className="font-semibold truncate">{result.title}</h3>
            <p className="text-xs text-[var(--muted-foreground)]">{result.categoryName}</p>
          </Link>
          <MatchBadge percentage={result.matchPercentage} className="shrink-0" />
        </div>

        {result.averageRating > 0 && (
          <div className="flex items-center gap-2">
            <RatingStars rating={result.averageRating} size={14} />
            <span className="text-xs text-[var(--muted-foreground)]">{result.averageRating.toFixed(1)}</span>
          </div>
        )}

        <div className="grid grid-cols-2 gap-3 text-sm">
          <MatchedIngredientList ingredients={result.matchedIngredients} />
          <MissingIngredientList ingredients={result.missingIngredients} />
        </div>

        <div className="flex items-center justify-between pt-1">
          {onToggleFavorite && (
            <FavoriteButton
              isFavorited={isFavorited}
              onToggle={onToggleFavorite}
              isLoading={isFavoriteLoading}
            />
          )}
          <Link
            to={`/recipes/${result.recipeId}`}
            className="text-sm font-medium text-[var(--primary)] hover:underline"
          >
            View Recipe &rarr;
          </Link>
        </div>
      </div>
    </Card>
  )
}

export function PantryResultCardSkeleton() {
  return (
    <Card className="overflow-hidden">
      <Skeleton className="h-[180px] w-full rounded-none" />
      <div className="p-4 space-y-3">
        <div className="flex justify-between">
          <div className="space-y-1">
            <Skeleton className="h-5 w-40" />
            <Skeleton className="h-3 w-24" />
          </div>
          <Skeleton className="h-5 w-20 rounded-full" />
        </div>
        <Skeleton className="h-4 w-32" />
        <div className="grid grid-cols-2 gap-3">
          <div className="space-y-1">
            <Skeleton className="h-3 w-16" />
            <Skeleton className="h-4 w-20" />
            <Skeleton className="h-4 w-16" />
          </div>
          <div className="space-y-1">
            <Skeleton className="h-3 w-14" />
            <Skeleton className="h-4 w-24" />
            <Skeleton className="h-4 w-20" />
          </div>
        </div>
        <div className="flex justify-between">
          <Skeleton className="h-8 w-20 rounded-full" />
          <Skeleton className="h-4 w-24" />
        </div>
      </div>
    </Card>
  )
}
