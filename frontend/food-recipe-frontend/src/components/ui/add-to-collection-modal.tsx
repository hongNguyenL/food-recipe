import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { collectionsApi } from '@/api/collections'
import { Button } from '@/components/ui/button'
import { LoadingSpinner } from '@/components/ui/loading-spinner'
import { Check, Plus } from 'lucide-react'
import toast from 'react-hot-toast'

interface AddToCollectionModalProps {
  recipeId: number
  recipeTitle: string
  isOpen: boolean
  onClose: () => void
  onSuccess?: () => void
}

export function AddToCollectionModal({ recipeId, recipeTitle, isOpen, onClose, onSuccess }: AddToCollectionModalProps) {
  const [addingTo, setAddingTo] = useState<number | null>(null)

  const { data, isLoading, isError, refetch } = useQuery({
    queryKey: ['my-collections'],
    queryFn: () => collectionsApi.getMyCollections({ size: 100 }),
    enabled: isOpen,
  })

  const collections = data?.data?.content || []

  const handleAdd = async (collectionId: number) => {
    setAddingTo(collectionId)
    try {
      await collectionsApi.addRecipe(collectionId, recipeId)
      toast.success(`"${recipeTitle}" added to collection`)
      onSuccess?.()
    } catch (err: any) {
      const msg = err?.response?.data?.message || 'Failed to add recipe'
      toast.error(msg)
    } finally {
      setAddingTo(null)
    }
  }

  if (!isOpen) return null

  return (
    <>
      <div className="fixed inset-0 z-50 bg-black/50" onClick={onClose} />
      <div className="fixed left-1/2 top-1/2 z-50 w-full max-w-md -translate-x-1/2 -translate-y-1/2 rounded-lg border border-[var(--border)] bg-[var(--background)] p-6 shadow-lg max-h-[80vh] flex flex-col">
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-lg font-semibold">Add to Collection</h3>
          <button onClick={onClose} className="text-[var(--muted-foreground)] hover:text-[var(--foreground)] text-sm">Close</button>
        </div>

        <p className="text-sm text-[var(--muted-foreground)] mb-4">
          Save <strong>{recipeTitle}</strong> to one of your collections:
        </p>

        <div className="flex-1 overflow-y-auto space-y-2">
          {isLoading ? (
            <LoadingSpinner />
          ) : isError ? (
            <div className="text-center py-4">
              <p className="text-sm text-[var(--muted-foreground)] mb-2">Failed to load collections</p>
              <Button variant="outline" size="sm" onClick={() => refetch()}>Retry</Button>
            </div>
          ) : collections.length === 0 ? (
            <div className="text-center py-8">
              <p className="text-sm text-[var(--muted-foreground)]">You don't have any collections yet.</p>
            </div>
          ) : (
            collections.map((col) => {
              const isLoadingThis = addingTo === col.id
              return (
                <button
                  key={col.id}
                  onClick={() => handleAdd(col.id)}
                  disabled={addingTo !== null}
                  className="w-full flex items-center justify-between rounded-md border border-[var(--border)] px-4 py-3 text-left text-sm hover:bg-[var(--accent)] transition-colors disabled:opacity-50"
                >
                  <div className="flex-1 min-w-0">
                    <p className="font-medium truncate">{col.name}</p>
                    <p className="text-xs text-[var(--muted-foreground)]">
                      {col.recipeCount} recipes · {col.visibility === 'PUBLIC' ? 'Public' : 'Private'}
                    </p>
                  </div>
                  {isLoadingThis ? (
                    <LoadingSpinner />
                  ) : (
                    <Plus size={18} className="shrink-0 text-[var(--muted-foreground)]" />
                  )}
                </button>
              )
            })
          )}
        </div>
      </div>
    </>
  )
}
