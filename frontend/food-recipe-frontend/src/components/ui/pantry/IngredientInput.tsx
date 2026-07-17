import { useState, type KeyboardEvent } from 'react'
import { Plus } from 'lucide-react'
import { IngredientChip } from './IngredientChip'

interface IngredientInputProps {
  ingredients: string[]
  onAdd: (ingredient: string) => void
  onRemove: (index: number) => void
  maxIngredients?: number
}

export function IngredientInput({ ingredients, onAdd, onRemove, maxIngredients = 30 }: IngredientInputProps) {
  const [value, setValue] = useState('')
  const [error, setError] = useState('')

  const addIngredient = (raw: string) => {
    const trimmed = raw.trim()
    if (!trimmed) return

    if (ingredients.length >= maxIngredients) {
      setError(`Maximum ${maxIngredients} ingredients allowed`)
      return
    }

    if (ingredients.some(i => i.toLowerCase() === trimmed.toLowerCase())) {
      setError('Ingredient already added')
      return
    }

    onAdd(trimmed)
    setValue('')
    setError('')
  }

  const handleKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault()
      addIngredient(value)
    }
  }

  const handleChange = (val: string) => {
    if (val.includes(',')) {
      const parts = val.split(',')
      parts.forEach(p => addIngredient(p))
      setValue('')
    } else {
      setValue(val)
    }
    if (error) setError('')
  }

  return (
    <div className="space-y-2">
      <div className="flex flex-wrap gap-2">
        {ingredients.map((ing, i) => (
          <IngredientChip key={i} label={ing} onRemove={() => onRemove(i)} />
        ))}
      </div>
      <div className="relative">
        <input
          type="text"
          value={value}
          onChange={(e) => handleChange(e.target.value)}
          onKeyDown={handleKeyDown}
          placeholder="Type an ingredient and press Enter, or paste comma-separated"
          className="w-full rounded-md border border-[var(--border)] bg-[var(--background)] px-3 py-2 pr-10 text-sm outline-none focus:border-[var(--primary)] focus:ring-1 focus:ring-[var(--primary)]"
        />
        <button
          onClick={() => addIngredient(value)}
          disabled={!value.trim()}
          className="absolute right-1 top-1/2 -translate-y-1/2 rounded-md p-1.5 text-[var(--muted-foreground)] hover:text-[var(--foreground)] disabled:opacity-40 transition-colors"
        >
          <Plus size={18} />
        </button>
      </div>
      {error && <p className="text-sm text-red-500">{error}</p>}
    </div>
  )
}
