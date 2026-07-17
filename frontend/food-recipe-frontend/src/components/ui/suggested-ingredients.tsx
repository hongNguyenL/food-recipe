const COMMON_INGREDIENTS = [
  'Egg', 'Milk', 'Butter', 'Chicken', 'Rice', 'Onion', 'Garlic', 'Tomato', 'Cheese', 'Flour',
  'Sugar', 'Salt', 'Pepper', 'Olive Oil', 'Potato', 'Carrot', 'Beef', 'Pork', 'Pasta', 'Bread',
]

interface SuggestedIngredientsProps {
  pantry: string[]
  onAdd: (ingredient: string) => void
}

export function SuggestedIngredients({ pantry, onAdd }: SuggestedIngredientsProps) {
  const inPantry = (name: string) => pantry.some((i) => i.toLowerCase() === name.toLowerCase())

  return (
    <div className="flex flex-wrap gap-2">
      <span className="text-xs text-[var(--muted-foreground)] self-center">Suggestions:</span>
      {COMMON_INGREDIENTS.map((name) => (
        <button
          key={name}
          type="button"
          disabled={inPantry(name)}
          onClick={() => onAdd(name)}
          className="rounded-full border border-[var(--border)] px-3 py-1 text-xs transition-colors hover:bg-[var(--accent)] disabled:opacity-30 disabled:cursor-not-allowed"
          aria-label={`Add ${name}`}
        >
          {name}
        </button>
      ))}
    </div>
  )
}
