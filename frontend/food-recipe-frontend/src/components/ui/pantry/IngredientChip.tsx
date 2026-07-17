import { X } from 'lucide-react'

interface IngredientChipProps {
  label: string
  onRemove: () => void
}

export function IngredientChip({ label, onRemove }: IngredientChipProps) {
  return (
    <span className="inline-flex items-center gap-1 rounded-full bg-[var(--primary)]/10 px-3 py-1 text-sm font-medium text-[var(--primary)]">
      {label}
      <button
        onClick={onRemove}
        className="ml-0.5 rounded-full p-0.5 hover:bg-[var(--primary)]/20 transition-colors"
      >
        <X size={14} />
      </button>
    </span>
  )
}
