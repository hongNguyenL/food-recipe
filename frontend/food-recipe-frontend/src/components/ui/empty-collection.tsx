import { BookOpen } from 'lucide-react'

interface EmptyCollectionProps {
  message: string
  actionLabel?: string
  onAction?: () => void
}

export function EmptyCollection({ message, actionLabel, onAction }: EmptyCollectionProps) {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-center">
      <BookOpen size={48} className="text-[var(--muted-foreground)] mb-4" />
      <p className="text-lg font-medium text-[var(--foreground)]">{message}</p>
      {actionLabel && onAction && (
        <button
          onClick={onAction}
          className="mt-4 text-sm text-[var(--primary)] hover:underline"
        >
          {actionLabel}
        </button>
      )}
    </div>
  )
}
