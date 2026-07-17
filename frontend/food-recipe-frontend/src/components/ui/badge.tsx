import { type ReactNode } from 'react'
import { cn } from '@/lib/utils'

interface BadgeProps {
  children: ReactNode
  className?: string
  variant?: 'default' | 'secondary' | 'outline'
}

export function Badge({ children, className, variant = 'default' }: BadgeProps) {
  const variants: Record<string, string> = {
    default: 'bg-[var(--primary)] text-[var(--primary-foreground)]',
    secondary: 'bg-[var(--secondary)] text-[var(--secondary-foreground)]',
    outline: 'border border-[var(--border)] text-[var(--foreground)]',
  }
  return (
    <span className={cn('inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-semibold transition-colors', variants[variant], className)}>
      {children}
    </span>
  )
}
