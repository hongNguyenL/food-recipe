import { Star } from 'lucide-react'
import { cn } from '@/lib/utils'

interface RatingStarsProps {
  rating: number
  size?: number
  interactive?: boolean
  onChange?: (rating: number) => void
}

export function RatingStars({ rating, size = 16, interactive = false, onChange }: RatingStarsProps) {
  return (
    <div className="flex items-center gap-0.5">
      {[1, 2, 3, 4, 5].map((star) => (
        <button
          key={star}
          type="button"
          disabled={!interactive}
          onClick={() => onChange?.(star)}
          className={cn(
            'transition-colors',
            interactive ? 'cursor-pointer hover:scale-110' : 'cursor-default',
          )}
        >
          <Star
            size={size}
            className={cn(
              'transition-colors',
              star <= Math.round(rating)
                ? 'fill-yellow-400 text-yellow-400'
                : 'text-[var(--muted-foreground)]',
            )}
          />
        </button>
      ))}
      {!interactive && <span className="ml-1 text-sm text-[var(--muted-foreground)]">{rating.toFixed(1)}</span>}
    </div>
  )
}
