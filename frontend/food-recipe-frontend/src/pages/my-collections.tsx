import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { collectionsApi } from '@/api/collections'
import { CollectionGrid } from '@/components/ui/collection-grid'
import { Pagination } from '@/components/ui/pagination'
import { DeleteCollectionDialog } from '@/components/ui/delete-collection-dialog'
import { Button } from '@/components/ui/button'
import { Plus } from 'lucide-react'
import toast from 'react-hot-toast'

export default function MyCollectionsPage() {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [page, setPage] = useState(0)
  const [deleteTarget, setDeleteTarget] = useState<{ id: number; name: string } | null>(null)

  const { data, isLoading, isError, refetch } = useQuery({
    queryKey: ['my-collections', page],
    queryFn: () => collectionsApi.getMyCollections({ page, size: 20 }),
  })

  const deleteMutation = useMutation({
    mutationFn: (id: number) => collectionsApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['my-collections'] })
      toast.success('Collection deleted')
      setDeleteTarget(null)
    },
    onError: (err: any) => {
      toast.error(err?.response?.data?.message || 'Failed to delete collection')
    },
  })

  const pageData = data?.data
  const collections = pageData?.content || []

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">My Collections</h1>
          <p className="text-[var(--muted-foreground)]">Manage your recipe collections</p>
        </div>
        <Link to="/my-collections/new">
          <Button>
            <Plus size={16} />
            New Collection
          </Button>
        </Link>
      </div>

      <CollectionGrid
        collections={collections}
        isLoading={isLoading}
        isError={isError}
        onRetry={() => refetch()}
        emptyMessage="You haven't created any collections yet"
      />

      {pageData && pageData.totalPages > 1 && (
        <Pagination page={pageData.number} totalPages={pageData.totalPages} onPageChange={setPage} />
      )}

      <DeleteCollectionDialog
        collectionName={deleteTarget?.name || ''}
        isOpen={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        onConfirm={() => deleteTarget && deleteMutation.mutate(deleteTarget.id)}
        isLoading={deleteMutation.isPending}
      />
    </div>
  )
}
