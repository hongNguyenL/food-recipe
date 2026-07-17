import { Badge } from '@/components/ui/badge'
import { Globe, Lock } from 'lucide-react'

interface VisibilityBadgeProps {
  visibility: 'PUBLIC' | 'PRIVATE'
}

export function VisibilityBadge({ visibility }: VisibilityBadgeProps) {
  return (
    <Badge variant={visibility === 'PUBLIC' ? 'default' : 'secondary'}>
      <span className="flex items-center gap-1">
        {visibility === 'PUBLIC' ? <Globe size={12} /> : <Lock size={12} />}
        {visibility === 'PUBLIC' ? 'Public' : 'Private'}
      </span>
    </Badge>
  )
}
