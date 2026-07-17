import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { adminApi } from '@/api/admin'
import { DataTable } from '@/components/ui/data-table'
import { Pagination } from '@/components/ui/pagination'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Modal } from '@/components/ui/modal'
import { LoadingSpinner } from '@/components/ui/loading-spinner'
import { ErrorComponent } from '@/components/ui/error-component'
import toast from 'react-hot-toast'
import { Trash2, Search } from 'lucide-react'
import type { CommentResponse, Page } from '@/types'

export default function AdminCommentsPage() {
  const queryClient = useQueryClient()
  const [searchParams, setSearchParams] = useState(() => new URLSearchParams())
  const keyword = searchParams.get('keyword') || ''
  const page = parseInt(searchParams.get('page') || '0', 10)
  const [searchInput, setSearchInput] = useState(keyword)

  const [deleteTarget, setDeleteTarget] = useState<CommentResponse | null>(null)

  const { data, isLoading, isError, refetch } = useQuery({
    queryKey: ['admin', 'comments', { keyword, page }],
    queryFn: async () => {
      const res = await adminApi.getComments({ keyword: keyword || undefined, page, size: 10 })
      return res.data as Page<CommentResponse>
    },
  })

  const deleteMutation = useMutation({
    mutationFn: async (id: number) => {
      await adminApi.deleteComment(id)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'comments'] })
      toast.success('Comment deleted')
      setDeleteTarget(null)
    },
    onError: (err: any) => {
      toast.error(err?.response?.data?.message || 'Failed to delete comment')
    },
  })

  const handleSearch = () => {
    setSearchParams((prev) => {
      const next = new URLSearchParams(prev)
      if (searchInput) next.set('keyword', searchInput)
      else next.delete('keyword')
      next.delete('page')
      return next
    })
  }

  const handlePageChange = (p: number) => {
    setSearchParams((prev) => {
      const next = new URLSearchParams(prev)
      next.set('page', String(p))
      return next
    })
  }

  return (
    <div className="space-y-6 p-6">
      <div>
        <h1 className="text-2xl font-bold">Comments</h1>
        <p className="text-[var(--muted-foreground)]">Manage user comments</p>
      </div>

      <div className="flex gap-2">
        <Input
          value={searchInput}
          onChange={(e) => setSearchInput(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
          placeholder="Search comments..."
          className="max-w-xs"
        />
        <Button variant="outline" onClick={handleSearch}>
          <Search size={16} />
        </Button>
      </div>

      {isLoading ? (
        <LoadingSpinner />
      ) : isError ? (
        <ErrorComponent message="Failed to load comments" onRetry={() => refetch()} />
      ) : data ? (
        <>
          <DataTable
            columns={[
              { key: 'id', label: 'ID' },
              { key: 'username', label: 'User' },
              {
                key: 'content',
                label: 'Content',
                render: (item: CommentResponse) => (
                  <span className="line-clamp-2 max-w-xs">
                    {item.content.length > 100
                      ? `${item.content.slice(0, 100)}...`
                      : item.content}
                  </span>
                ),
              },
              { key: 'recipeId', label: 'Recipe ID' },
              {
                key: 'createdAt',
                label: 'Created At',
                render: (item: CommentResponse) =>
                  new Date(item.createdAt).toLocaleDateString(),
              },
              {
                key: 'actions',
                label: 'Actions',
                render: (item: CommentResponse) => (
                  <Button
                    variant="destructive"
                    size="sm"
                    onClick={() => setDeleteTarget(item)}
                  >
                    <Trash2 size={14} />
                  </Button>
                ),
              },
            ]}
            data={data.content}
            keyExtractor={(item) => item.id}
          />
          <Pagination
            page={data.number}
            totalPages={data.totalPages}
            onPageChange={handlePageChange}
          />
        </>
      ) : null}

      <Modal
        isOpen={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        title="Delete Comment"
      >
        <p className="mb-4 text-sm">
          Are you sure you want to delete this comment? This action cannot be undone.
        </p>
        <div className="mb-4 rounded-md bg-[var(--muted)] p-3 text-sm">
          {deleteTarget?.content}
        </div>
        <div className="flex justify-end gap-2">
          <Button variant="outline" onClick={() => setDeleteTarget(null)}>
            Cancel
          </Button>
          <Button
            variant="destructive"
            isLoading={deleteMutation.isPending}
            onClick={() => deleteTarget && deleteMutation.mutate(deleteTarget.id)}
          >
            Delete
          </Button>
        </div>
      </Modal>
    </div>
  )
}
