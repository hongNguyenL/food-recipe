import { Refrigerator, SearchX, AlertCircle } from 'lucide-react'
import { Button } from '@/components/ui/button'

export function EmptyPantryState() {
  return (
    <div className="flex flex-col items-center justify-center py-20 text-center">
      <Refrigerator size={64} className="text-[var(--muted-foreground)] mb-4" />
      <h2 className="text-xl font-semibold">What's in My Fridge?</h2>
      <p className="mt-2 text-[var(--muted-foreground)] max-w-md">
        Add some ingredients above to discover recipes you can make with what you already have.
      </p>
    </div>
  )
}

interface NoResultsStateProps {
  onClear: () => void
}

export function NoResultsState({ onClear }: NoResultsStateProps) {
  return (
    <div className="flex flex-col items-center justify-center py-20 text-center">
      <SearchX size={48} className="text-[var(--muted-foreground)] mb-4" />
      <h2 className="text-xl font-semibold">No recipes matched your pantry</h2>
      <ul className="mt-2 text-sm text-[var(--muted-foreground)] space-y-1">
        <li>Try removing some ingredients</li>
        <li>Reduce the minimum match percentage</li>
        <li>Search with fewer ingredients</li>
      </ul>
      <Button variant="outline" className="mt-6" onClick={onClear}>
        Clear All Filters
      </Button>
    </div>
  )
}

export function SearchSkeleton() {
  return (
    <div className="space-y-4">
      {Array.from({ length: 3 }).map((_, i) => (
        <div key={i} className="flex flex-col sm:flex-row overflow-hidden rounded-lg border border-[var(--border)] animate-pulse">
          <div className="h-48 sm:w-48 bg-[var(--muted)]" />
          <div className="flex-1 p-4 space-y-3">
            <div className="h-5 w-3/4 rounded bg-[var(--muted)]" />
            <div className="h-4 w-1/4 rounded bg-[var(--muted)]" />
            <div className="h-3 w-full rounded bg-[var(--muted)]" />
            <div className="h-3 w-2/3 rounded bg-[var(--muted)]" />
            <div className="flex gap-6">
              <div className="space-y-1">
                <div className="h-3 w-16 rounded bg-[var(--muted)]" />
                <div className="h-3 w-20 rounded bg-[var(--muted)]" />
              </div>
              <div className="space-y-1">
                <div className="h-3 w-12 rounded bg-[var(--muted)]" />
                <div className="h-3 w-24 rounded bg-[var(--muted)]" />
              </div>
            </div>
          </div>
        </div>
      ))}
    </div>
  )
}

interface ErrorMessageProps {
  message?: string
  onRetry: () => void
}

export function ErrorMessage({ message = 'Something went wrong', onRetry }: ErrorMessageProps) {
  return (
    <div className="flex flex-col items-center justify-center py-20 text-center">
      <AlertCircle size={48} className="text-red-500 mb-4" />
      <h2 className="text-xl font-semibold">{message}</h2>
      <p className="mt-2 text-sm text-[var(--muted-foreground)]">Please try again.</p>
      <Button variant="outline" className="mt-6" onClick={onRetry}>
        Retry
      </Button>
    </div>
  )
}
