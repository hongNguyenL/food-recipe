import { Check, X } from 'lucide-react'

interface IngredientMatchListProps {
  ingredients: string[]
  type: 'matched' | 'missing'
}

export function MatchedIngredientList({ ingredients }: { ingredients: string[] }) {
  if (ingredients.length === 0) return null
  return (
    <div className="space-y-0.5">
      <p className="text-xs font-medium text-green-600 dark:text-green-400">You Have</p>
      {ingredients.map((ing) => (
        <p key={ing} className="flex items-center gap-1 text-xs text-[var(--foreground)]">
          <Check size={12} className="shrink-0 text-green-500" />
          {ing}
        </p>
      ))}
    </div>
  )
}

export function MissingIngredientList({ ingredients }: { ingredients: string[] }) {
  if (ingredients.length === 0) return null
  return (
    <div className="space-y-0.5">
      <p className="text-xs font-medium text-red-600 dark:text-red-400">You Need</p>
      {ingredients.map((ing) => (
        <p key={ing} className="flex items-center gap-1 text-xs text-[var(--muted-foreground)]">
          <X size={12} className="shrink-0 text-red-400" />
          {ing}
        </p>
      ))}
    </div>
  )
}
