interface PaginationProps {
  page: number
  totalPages: number
  onPageChange: (page: number) => void
}

export function Pagination({ page, totalPages, onPageChange }: PaginationProps) {
  if (totalPages <= 1) return null

  const pages: (number | string)[] = []
  const delta = 2
  const start = Math.max(0, page - delta)
  const end = Math.min(totalPages - 1, page + delta)

  if (start > 0) {
    pages.push(0)
    if (start > 1) pages.push('...')
  }
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  if (end < totalPages - 1) {
    if (end < totalPages - 2) pages.push('...')
    pages.push(totalPages - 1)
  }

  return (
    <div className="flex items-center justify-center gap-1">
      <button
        onClick={() => onPageChange(page - 1)}
        disabled={page === 0}
        className="inline-flex h-8 w-8 items-center justify-center rounded-md text-sm transition-colors hover:bg-[var(--accent)] disabled:pointer-events-none disabled:opacity-50"
      >
        &lt;
      </button>
      {pages.map((p, idx) =>
        typeof p === 'string' ? (
          <span key={`ellipsis-${idx}`} className="px-1 text-sm">...</span>
        ) : (
          <button
            key={p}
            onClick={() => onPageChange(p)}
            className={`inline-flex h-8 w-8 items-center justify-center rounded-md text-sm transition-colors ${
              p === page
                ? 'bg-[var(--primary)] text-[var(--primary-foreground)]'
                : 'hover:bg-[var(--accent)]'
            }`}
          >
            {p + 1}
          </button>
        ),
      )}
      <button
        onClick={() => onPageChange(page + 1)}
        disabled={page >= totalPages - 1}
        className="inline-flex h-8 w-8 items-center justify-center rounded-md text-sm transition-colors hover:bg-[var(--accent)] disabled:pointer-events-none disabled:opacity-50"
      >
        &gt;
      </button>
    </div>
  )
}
