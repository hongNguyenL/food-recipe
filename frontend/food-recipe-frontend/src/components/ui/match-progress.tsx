interface MatchProgressProps {
  percentage: number
}

function getColorClass(pct: number): string {
  if (pct >= 90) return 'bg-green-500'
  if (pct >= 70) return 'bg-lime-500'
  if (pct >= 50) return 'bg-yellow-500'
  return 'bg-orange-400'
}

function getLabel(pct: number): string {
  if (pct >= 90) return 'Excellent'
  if (pct >= 70) return 'Good'
  if (pct >= 50) return 'Fair'
  return 'Low Match'
}

export function MatchProgress({ percentage }: MatchProgressProps) {
  return (
    <div className="space-y-1">
      <div className="flex items-center justify-between text-xs">
        <span className="font-medium">{percentage}% Match</span>
        <span className="text-[var(--muted-foreground)]">{getLabel(percentage)}</span>
      </div>
      <div className="h-2 w-full overflow-hidden rounded-full bg-[var(--muted)]">
        <div
          className={`h-full rounded-full transition-all ${getColorClass(percentage)}`}
          style={{ width: `${percentage}%` }}
        />
      </div>
    </div>
  )
}
