import { CollectionCard } from '@/components/ui/collection-card'
import { CollectionCardSkeleton } from '@/components/ui/collection-card-skeleton'
import { EmptyCollection } from '@/components/ui/empty-collection'
import type { CollectionSummaryResponse } from '@/types'

interface CollectionGridProps {
  collections: CollectionSummaryResponse[]
  isLoading: boolean
  isError: boolean
  onRetry?: () => void
  showOwner?: boolean
  emptyMessage?: string
}

export function CollectionGrid({ collections, isLoading, isError, onRetry, showOwner, emptyMessage }: CollectionGridProps) {
  if (isLoading) {
    return (
      <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        {Array.from({ length: 8 }).map((_, i) => (
          <CollectionCardSkeleton key={i} />
        ))}
      </div>
    )
  }

  if (isError) {
    return <EmptyCollection message="Failed to load collections" actionLabel="Try again" onAction={onRetry} />
  }

  if (collections.length === 0) {
    return <EmptyCollection message={emptyMessage || 'No collections yet'} />
  }

  return (
    <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
      {collections.map((collection) => (
        <CollectionCard key={collection.id} collection={collection} showOwner={showOwner} />
      ))}
    </div>
  )
}
