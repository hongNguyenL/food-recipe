import { type ReactNode } from 'react'

interface Column<T> {
  key: string
  label: string
  render?: (item: T) => ReactNode
}

interface DataTableProps<T> {
  columns: Column<T>[]
  data: T[]
  keyExtractor: (item: T) => string | number
  isLoading?: boolean
}

export function DataTable<T>({ columns, data, keyExtractor, isLoading }: DataTableProps<T>) {
  if (isLoading) {
    return (
      <div className="rounded-md border border-[var(--border)]">
        <div className="p-8 text-center text-[var(--muted-foreground)]">Loading...</div>
      </div>
    )
  }

  if (data.length === 0) {
    return (
      <div className="rounded-md border border-[var(--border)]">
        <div className="p-8 text-center text-[var(--muted-foreground)]">No data found.</div>
      </div>
    )
  }

  return (
    <div className="overflow-x-auto rounded-md border border-[var(--border)]">
      <table className="w-full text-sm">
        <thead>
          <tr className="border-b border-[var(--border)] bg-[var(--muted)]">
            {columns.map((col) => (
              <th key={col.key} className="px-4 py-3 text-left font-medium">
                {col.label}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {data.map((item) => (
            <tr key={keyExtractor(item)} className="border-b border-[var(--border)] last:border-0 hover:bg-[var(--accent)]/50">
              {columns.map((col) => (
                <td key={col.key} className="px-4 py-3">
                  {col.render ? col.render(item) : String((item as Record<string, unknown>)[col.key] ?? '')}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
