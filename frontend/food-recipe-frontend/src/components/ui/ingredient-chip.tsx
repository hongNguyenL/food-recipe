import { X } from 'lucide-react'

interface IngredientChipProps {
  ingredient: string
  onRemove: (ingredient: string) => void
}

export function IngredientChip({ ingredient, onRemove }: IngredientChipProps) {
  return (
    <span className="inline-flex items-center gap-1 rounded-full border border-[var(--border)] bg-[var(--accent)] px-3 py-1 text-sm">
      {ingredient}
      <button
        type="button"
        onClick={() => onRemove(ingredient)}
        className="rounded-full p-0.5 hover:bg-[var(--muted)] transition-colors"
        aria-label={`Remove ${ingredient}`}
      >
        <X size={14} />
      </button>
    </span>
  )
}
