import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { categoriesApi } from '@/api/categories'
import { Card, CardContent } from '@/components/ui/card'
import { LoadingSpinner } from '@/components/ui/loading-spinner'
import { ErrorComponent } from '@/components/ui/error-component'
import { FolderOpen } from 'lucide-react'

export default function Categories() {
  const query = useQuery({
    queryKey: ['categories'],
    queryFn: () => categoriesApi.list(),
  })

  if (query.isPending) {
    return <LoadingSpinner />
  }

  if (query.isError) {
    return <ErrorComponent message="Failed to load categories" onRetry={() => query.refetch()} />
  }

  const categories = query.data.data

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-3xl font-bold">Categories</h1>
        <p className="mt-2 text-[var(--muted-foreground)]">Browse recipes by category</p>
      </div>

      <div className="grid gap-6 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
        {categories.map((category) => (
          <Link key={category.id} to={`/categories/${category.id}`}>
            <Card className="group cursor-pointer transition-shadow hover:shadow-md">
              <CardContent className="flex flex-col items-center gap-4 py-12">
                <FolderOpen
                  size={48}
                  className="text-[var(--muted-foreground)] transition-colors group-hover:text-[var(--primary)]"
                />
                <h3 className="text-lg font-semibold">{category.name}</h3>
              </CardContent>
            </Card>
          </Link>
        ))}
      </div>

      {categories.length === 0 && (
        <p className="text-center text-[var(--muted-foreground)]">No categories found.</p>
      )}
    </div>
  )
}
