import { X } from 'lucide-react'

interface FilterSummaryProps {
  ingredients: string[]
  minMatch: number
  categoryName: string | null
  onClear: () => void
}

export function FilterSummary({ ingredients, minMatch, categoryName, onClear }: FilterSummaryProps) {
  const hasFilters = ingredients.length > 0 || minMatch > 0 || categoryName

  if (!hasFilters) return null

  return (
    <div className="rounded-lg border border-[var(--border)] p-3 space-y-2">
      <div className="flex items-center justify-between">
        <span className="text-sm font-medium">Active Filters</span>
        <button
          onClick={onClear}
          className="text-xs text-[var(--primary)] hover:underline flex items-center gap-1"
          aria-label="Clear all filters"
        >
          <X size={14} />
          Clear Filters
        </button>
      </div>
      <div className="flex flex-wrap gap-2">
        {ingredients.map((ing) => (
          <span
            key={ing}
            className="inline-flex items-center gap-1 rounded-full bg-[var(--primary)]/10 px-2.5 py-0.5 text-xs font-medium text-[var(--primary)]"
          >
            {ing}
          </span>
        ))}
        {minMatch > 0 && (
          <span className="inline-flex items-center gap-1 rounded-full bg-[var(--muted)] px-2.5 py-0.5 text-xs font-medium text-[var(--muted-foreground)]">
            Min Match: {minMatch}%
          </span>
        )}
        {categoryName && (
          <span className="inline-flex items-center gap-1 rounded-full bg-[var(--muted)] px-2.5 py-0.5 text-xs font-medium text-[var(--muted-foreground)]">
            {categoryName}
          </span>
        )}
      </div>
    </div>
  )
}
