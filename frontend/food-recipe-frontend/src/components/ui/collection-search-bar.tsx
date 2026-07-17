import { SearchBar } from '@/components/ui/search-bar'

interface CollectionSearchBarProps {
  value: string
  onChange: (value: string) => void
  onClear: () => void
}

export function CollectionSearchBar({ value, onChange, onClear }: CollectionSearchBarProps) {
  return (
    <div className="w-full max-w-md">
      <SearchBar
        value={value}
        onChange={onChange}
        onClear={onClear}
      />
    </div>
  )
}
