import { Button } from '@/components/ui/button'

interface DeleteCollectionDialogProps {
  collectionName: string
  isOpen: boolean
  onClose: () => void
  onConfirm: () => void
  isLoading: boolean
}

export function DeleteCollectionDialog({ collectionName, isOpen, onClose, onConfirm, isLoading }: DeleteCollectionDialogProps) {
  if (!isOpen) return null

  return (
    <>
      <div className="fixed inset-0 z-50 bg-black/50" onClick={onClose} />
      <div className="fixed left-1/2 top-1/2 z-50 w-full max-w-md -translate-x-1/2 -translate-y-1/2 rounded-lg border border-[var(--border)] bg-[var(--background)] p-6 shadow-lg">
        <h3 className="text-lg font-semibold">Delete Collection</h3>
        <p className="mt-2 text-sm text-[var(--muted-foreground)]">
          Are you sure you want to delete <strong>{collectionName}</strong>? This action cannot be undone.
        </p>
        <div className="mt-6 flex justify-end gap-3">
          <Button variant="outline" onClick={onClose} disabled={isLoading}>
            Cancel
          </Button>
          <Button variant="destructive" onClick={onConfirm} isLoading={isLoading}>
            Delete
          </Button>
        </div>
      </div>
    </>
  )
}
