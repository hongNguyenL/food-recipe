import { cn } from '@/lib/utils'

interface MatchBadgeProps {
  percentage: number
  className?: string
}

export function MatchBadge({ percentage, className }: MatchBadgeProps) {
  const color =
    percentage >= 80 ? 'bg-green-100 text-green-700 dark:bg-green-900 dark:text-green-300' :
    percentage >= 50 ? 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900 dark:text-yellow-300' :
                      'bg-red-100 text-red-700 dark:bg-red-900 dark:text-red-300'

  return (
    <span className={cn('inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-bold', color, className)}>
      {percentage}% Match
    </span>
  )
}
