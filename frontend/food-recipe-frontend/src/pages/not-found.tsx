import { Link } from 'react-router-dom'
import { Home } from 'lucide-react'
import { Button } from '@/components/ui/button'

export default function NotFoundPage() {
  return (
    <div className="flex flex-col items-center justify-center min-h-[60vh] text-center space-y-4">
      <h1 className="text-6xl font-bold text-[var(--muted-foreground)]">404</h1>
      <h2 className="text-2xl font-semibold">Page Not Found</h2>
      <p className="text-[var(--muted-foreground)] max-w-md">
        The page you are looking for does not exist or has been moved.
      </p>
      <Link to="/">
        <Button>
          <Home className="mr-2 h-4 w-4" />
          Back to Home
        </Button>
      </Link>
    </div>
  )
}
