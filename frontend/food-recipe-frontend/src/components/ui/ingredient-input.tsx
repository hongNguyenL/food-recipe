import { useState, type KeyboardEvent } from 'react'
import { IngredientChip } from '@/components/ui/ingredient-chip'

interface IngredientInputProps {
  ingredients: string[]
  onAdd: (ingredient: string) => void
  onRemove: (ingredient: string) => void
  maxIngredients?: number
}

export function IngredientInput({ ingredients, onAdd, onRemove, maxIngredients = 30 }: IngredientInputProps) {
  const [value, setValue] = useState('')

  const addIngredient = (raw: string) => {
    const trimmed = raw.trim()
    if (!trimmed) return
    if (ingredients.length >= maxIngredients) return
    const exists = ingredients.some((i) => i.toLowerCase() === trimmed.toLowerCase())
    if (exists) return
    onAdd(trimmed)
  }

  const handleKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault()
      addIngredient(value)
      setValue('')
      return
    }
    if (e.key === ',' || e.key === '،') {
      e.preventDefault()
      const parts = value.split(',')
      parts.forEach((part) => addIngredient(part))
      setValue('')
      return
    }
  }

  const handlePaste = (e: React.ClipboardEvent) => {
    const text = e.clipboardData.getData('text')
    if (text.includes(',') || text.includes('\n')) {
      e.preventDefault()
      const parts = text.split(/[,،\n]+/)
      parts.forEach((part) => addIngredient(part))
      setValue('')
    }
  }

  return (
    <div className="space-y-2">
      <div className="flex flex-wrap gap-2">
        {ingredients.map((ing) => (
          <IngredientChip key={ing} ingredient={ing} onRemove={onRemove} />
        ))}
      </div>
      <input
        type="text"
        value={value}
        onChange={(e) => setValue(e.target.value)}
        onKeyDown={handleKeyDown}
        onPaste={handlePaste}
        placeholder={ingredients.length === 0 ? 'Type an ingredient and press Enter...' : 'Add more ingredients...'}
        className="flex h-10 w-full rounded-md border border-[var(--input)] bg-[var(--background)] px-3 py-2 text-sm placeholder:text-[var(--muted-foreground)] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[var(--ring)]"
        aria-label="Add ingredient"
      />
      <p className="text-xs text-[var(--muted-foreground)]">
        {ingredients.length}/{maxIngredients} ingredients • Press Enter or use commas to add
      </p>
    </div>
  )
}
