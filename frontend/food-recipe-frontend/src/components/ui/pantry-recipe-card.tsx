import { useState } from 'react'
import { Link } from 'react-router-dom'
import { Card, CardContent } from '@/components/ui/card'
import { RatingStars } from '@/components/ui/rating-stars'
import { MatchBadge } from '@/components/ui/match-badge'
import { MatchProgress } from '@/components/ui/match-progress'
import { MatchedIngredientList, MissingIngredientList } from '@/components/ui/ingredient-match-list'
import { Button } from '@/components/ui/button'
import { ArrowRight } from 'lucide-react'
import type { PantrySearchResult } from '@/types'

interface PantryRecipeCardProps {
  result: PantrySearchResult
}

export function PantryRecipeCard({ result }: PantryRecipeCardProps) {
  const [imgError, setImgError] = useState(false)

  return (
    <Card className="overflow-hidden transition-shadow hover:shadow-md">
      <div className="flex flex-col sm:flex-row">
        <Link to={`/recipes/${result.recipeId}`} className="sm:w-48 shrink-0">
          <div className="h-48 sm:h-full w-full overflow-hidden bg-[var(--muted)]">
            {result.imageUrl && !imgError ? (
              <img
                src={result.imageUrl}
                alt={result.title}
                className="h-full w-full object-cover"
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

        <CardContent className="flex flex-1 flex-col gap-3 p-4">
          <div className="flex items-start justify-between gap-2">
            <div className="min-w-0">
              <Link to={`/recipes/${result.recipeId}`} className="hover:underline">
                <h3 className="font-semibold truncate">{result.title}</h3>
              </Link>
              <p className="text-xs text-[var(--muted-foreground)]">{result.categoryName}</p>
              {result.averageRating > 0 && (
                <div className="mt-1 flex items-center gap-1">
                  <RatingStars rating={result.averageRating} size={12} />
                  <span className="text-xs text-[var(--muted-foreground)]">({result.averageRating.toFixed(1)})</span>
                </div>
              )}
            </div>
            <MatchBadge percentage={result.matchPercentage} />
          </div>

          <MatchProgress percentage={result.matchPercentage} />

          <div className="flex gap-6">
            <MatchedIngredientList ingredients={result.matchedIngredients} />
            <MissingIngredientList ingredients={result.missingIngredients} />
          </div>

          <div className="mt-auto">
            <Link to={`/recipes/${result.recipeId}`}>
              <Button variant="outline" size="sm" className="w-full sm:w-auto">
                View Recipe
                <ArrowRight size={14} />
              </Button>
            </Link>
          </div>
        </CardContent>
      </div>
    </Card>
  )
}
