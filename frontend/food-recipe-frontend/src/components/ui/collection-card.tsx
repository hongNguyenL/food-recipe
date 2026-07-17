import { Link } from 'react-router-dom'
import { Card, CardContent } from '@/components/ui/card'
import { VisibilityBadge } from '@/components/ui/visibility-badge'
import { BookOpen, Calendar } from 'lucide-react'
import type { CollectionSummaryResponse } from '@/types'

interface CollectionCardProps {
  collection: CollectionSummaryResponse
  showOwner?: boolean
}

export function CollectionCard({ collection, showOwner = false }: CollectionCardProps) {
  const date = new Date(collection.createdAt).toLocaleDateString()

  return (
    <Link to={`/collections/${collection.id}`}>
      <Card className="group h-full cursor-pointer transition-shadow hover:shadow-md">
        <CardContent className="flex flex-col gap-3 p-5">
          <div className="flex items-start justify-between gap-2">
            <h3 className="font-semibold truncate">{collection.name}</h3>
            <VisibilityBadge visibility={collection.visibility} />
          </div>

          {collection.description && (
            <p className="text-sm text-[var(--muted-foreground)] line-clamp-2">
              {collection.description}
            </p>
          )}

          <div className="mt-auto flex items-center justify-between text-xs text-[var(--muted-foreground)]">
            <span className="flex items-center gap-1">
              <BookOpen size={14} />
              {collection.recipeCount} {collection.recipeCount === 1 ? 'recipe' : 'recipes'}
            </span>
            <span className="flex items-center gap-1">
              <Calendar size={14} />
              {date}
            </span>
          </div>

          {showOwner && (
            <p className="text-xs text-[var(--muted-foreground)]">
              by {collection.ownerUsername}
            </p>
          )}
        </CardContent>
      </Card>
    </Link>
  )
}
