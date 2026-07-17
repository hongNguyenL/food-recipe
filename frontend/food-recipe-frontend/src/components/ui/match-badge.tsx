interface MatchBadgeProps {
  percentage: number
}

function getColor(pct: number): string {
  if (pct >= 90) return 'text-green-600 bg-green-50 dark:bg-green-950'
  if (pct >= 70) return 'text-lime-600 bg-lime-50 dark:bg-lime-950'
  if (pct >= 50) return 'text-yellow-600 bg-yellow-50 dark:bg-yellow-950'
  return 'text-orange-600 bg-orange-50 dark:bg-orange-950'
}

export function MatchBadge({ percentage }: MatchBadgeProps) {
  return (
    <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-semibold ${getColor(percentage)}`}>
      {percentage}%
    </span>
  )
}
