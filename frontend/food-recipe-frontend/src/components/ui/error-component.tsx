import { AlertCircle, RefreshCw } from 'lucide-react'
import { Button } from './button'

interface ErrorComponentProps {
  message?: string
  onRetry?: () => void
}

export function ErrorComponent({ message = 'Something went wrong', onRetry }: ErrorComponentProps) {
  return (
    <div className="flex flex-col items-center justify-center gap-4 p-8 text-center">
      <AlertCircle className="h-12 w-12 text-red-500" />
      <p className="text-lg font-medium">{message}</p>
      {onRetry && (
        <Button variant="outline" onClick={onRetry}>
          <RefreshCw className="mr-2 h-4 w-4" />
          Try Again
        </Button>
      )}
    </div>
  )
}
