import { Loader2 } from 'lucide-react'

export function LoadingSpinner({ size = 24 }: { size?: number }) {
  return (
    <div className="flex items-center justify-center p-8">
      <Loader2 className="animate-spin text-[var(--primary)]" size={size} />
    </div>
  )
}
