import { X } from 'lucide-react'

interface MissingIngredientListProps {
  ingredients: string[]
}

export function MissingIngredientList({ ingredients }: MissingIngredientListProps) {
  if (ingredients.length === 0) return null

  return (
    <div className="space-y-1">
      <p className="text-xs font-semibold text-red-500">You Need</p>
      {ingredients.map((ing, i) => (
        <div key={i} className="flex items-center gap-1.5 text-sm text-red-500">
          <X size={14} className="shrink-0" />
          <span>{ing}</span>
        </div>
      ))}
    </div>
  )
}
