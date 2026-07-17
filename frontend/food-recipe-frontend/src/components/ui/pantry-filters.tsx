import { Select } from '@/components/ui/select'
import { MatchPercentageSlider } from '@/components/ui/match-percentage-slider'

interface PantryFiltersProps {
  minMatch: number
  onMinMatchChange: (value: number) => void
  categoryId: string
  onCategoryChange: (value: string) => void
  pageSize: number
  onPageSizeChange: (value: number) => void
  categories: { id: number; name: string }[]
}

export function PantryFilters({
  minMatch, onMinMatchChange,
  categoryId, onCategoryChange,
  pageSize, onPageSizeChange,
  categories,
}: PantryFiltersProps) {
  return (
    <div className="flex flex-wrap items-end gap-4 rounded-lg border border-[var(--border)] p-4">
      <MatchPercentageSlider value={minMatch} onChange={onMinMatchChange} />

      <div className="w-40">
        <Select
          label="Category"
          value={categoryId}
          onChange={(e) => onCategoryChange(e.target.value)}
          options={[
            { value: '', label: 'All Categories' },
            ...categories.map((c) => ({ value: String(c.id), label: c.name })),
          ]}
        />
      </div>

      <div className="w-28">
        <Select
          label="Page Size"
          value={String(pageSize)}
          onChange={(e) => onPageSizeChange(Number(e.target.value))}
          options={[
            { value: '10', label: '10' },
            { value: '20', label: '20' },
            { value: '50', label: '50' },
          ]}
        />
      </div>
    </div>
  )
}
