import { Check } from 'lucide-react'

interface MatchedIngredientListProps {
  ingredients: string[]
}

export function MatchedIngredientList({ ingredients }: MatchedIngredientListProps) {
  if (ingredients.length === 0) return null

  return (
    <div className="space-y-1">
      <p className="text-xs font-semibold text-green-600 dark:text-green-400">You Have</p>
      {ingredients.map((ing, i) => (
        <div key={i} className="flex items-center gap-1.5 text-sm text-green-600 dark:text-green-400">
          <Check size={14} className="shrink-0" />
          <span>{ing}</span>
        </div>
      ))}
    </div>
  )
}
