import { Heart } from 'lucide-react'
import { cn } from '@/lib/utils'

interface FavoriteButtonProps {
  isFavorited?: boolean
  count?: number
  onToggle: () => void
  isLoading?: boolean
}

export function FavoriteButton({ isFavorited, count, onToggle, isLoading }: FavoriteButtonProps) {
  return (
    <button
      onClick={onToggle}
      disabled={isLoading}
      className={cn(
        'inline-flex items-center gap-1.5 rounded-full px-3 py-1.5 text-sm transition-colors',
        isFavorited
          ? 'bg-red-50 text-red-500 dark:bg-red-950'
          : 'bg-[var(--secondary)] text-[var(--secondary-foreground)] hover:bg-red-50 hover:text-red-500 dark:hover:bg-red-950',
      )}
    >
      <Heart
        size={16}
        className={cn(
          'transition-colors',
          isFavorited ? 'fill-red-500 text-red-500' : '',
        )}
      />
      {count !== undefined && <span>{count}</span>}
    </button>
  )
}
