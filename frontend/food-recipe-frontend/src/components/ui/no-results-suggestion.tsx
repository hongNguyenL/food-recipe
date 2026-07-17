import { SearchX, ArrowDown } from 'lucide-react'
import { Button } from '@/components/ui/button'

interface NoResultsSuggestionProps {
  minMatch: number
  onLowerThreshold: (value: number) => void
  onClear: () => void
}

const SUGGESTED_LOWER = 50

export function NoResultsSuggestion({ minMatch, onLowerThreshold, onClear }: NoResultsSuggestionProps) {
  return (
    <div className="flex flex-col items-center justify-center py-20 text-center">
      <SearchX size={48} className="text-[var(--muted-foreground)] mb-4" />
      <h2 className="text-xl font-semibold">
        No recipes matched at least {minMatch}%
      </h2>
      <p className="mt-2 text-[var(--muted-foreground)] max-w-md">
        Try lowering the minimum match percentage to discover more recipes.
      </p>
      {minMatch > SUGGESTED_LOWER && (
        <Button
          variant="outline"
          className="mt-4"
          onClick={() => onLowerThreshold(SUGGESTED_LOWER)}
        >
          <ArrowDown size={16} />
          Lower to 50%
        </Button>
      )}
      <Button variant="ghost" className="mt-2" onClick={onClear}>
        Clear All Filters
      </Button>
    </div>
  )
}
