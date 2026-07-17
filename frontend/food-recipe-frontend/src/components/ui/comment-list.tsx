import { useState } from 'react'
import { Pencil, Trash2 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Textarea } from '@/components/ui/textarea'
import { Modal } from '@/components/ui/modal'
import { Pagination } from '@/components/ui/pagination'
import type { CommentResponse } from '@/types'

interface CommentListProps {
  comments: CommentResponse[]
  currentUserId?: number
  isAdmin?: boolean
  page: number
  totalPages: number
  onPageChange: (page: number) => void
  onEdit: (id: number, content: string) => Promise<void>
  onDelete: (id: number) => Promise<void>
}

export function CommentList({
  comments,
  currentUserId,
  isAdmin,
  page,
  totalPages,
  onPageChange,
  onEdit,
  onDelete,
}: CommentListProps) {
  const [editingId, setEditingId] = useState<number | null>(null)
  const [editContent, setEditContent] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [deleteId, setDeleteId] = useState<number | null>(null)

  const handleSave = async () => {
    if (!editingId || !editContent.trim()) return
    setIsSubmitting(true)
    try {
      await onEdit(editingId, editContent)
      setEditingId(null)
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleDelete = async () => {
    if (deleteId === null) return
    try {
      await onDelete(deleteId)
    } finally {
      setDeleteId(null)
    }
  }

  if (comments.length === 0) {
    return <p className="text-[var(--muted-foreground)]">No comments yet.</p>
  }

  return (
    <div className="space-y-4">
      {comments.map((comment) => {
        const isOwner = comment.userId === currentUserId
        return (
          <div key={comment.id} className="rounded-lg border border-[var(--border)] p-4">
            {editingId === comment.id ? (
              <div className="space-y-2">
                <Textarea
                  value={editContent}
                  onChange={(e) => setEditContent(e.target.value)}
                  rows={3}
                />
                <div className="flex gap-2">
                  <Button size="sm" onClick={handleSave} isLoading={isSubmitting}>Save</Button>
                  <Button size="sm" variant="ghost" onClick={() => setEditingId(null)}>Cancel</Button>
                </div>
              </div>
            ) : (
              <>
                <div className="flex items-start justify-between">
                  <div>
                    <p className="text-sm font-medium">{comment.username}</p>
                    <p className="text-xs text-[var(--muted-foreground)]">
                      {new Date(comment.createdAt).toLocaleDateString()}
                    </p>
                  </div>
                  {(isOwner || isAdmin) && (
                    <div className="flex gap-1">
                      {isOwner && (
                        <button
                          onClick={() => { setEditingId(comment.id); setEditContent(comment.content) }}
                          className="rounded p-1 hover:bg-[var(--accent)] transition-colors"
                        >
                          <Pencil size={14} />
                        </button>
                      )}
                      <button
                        onClick={() => setDeleteId(comment.id)}
                        className="rounded p-1 hover:bg-red-100 dark:hover:bg-red-900 transition-colors text-red-500"
                      >
                        <Trash2 size={14} />
                      </button>
                    </div>
                  )}
                </div>
                <p className="mt-2 text-sm">{comment.content}</p>
              </>
            )}
          </div>
        )
      })}

      <Pagination page={page} totalPages={totalPages} onPageChange={onPageChange} />

      <Modal isOpen={deleteId !== null} onClose={() => setDeleteId(null)} title="Delete Comment">
        <p className="mb-4">Are you sure you want to delete this comment?</p>
        <div className="flex justify-end gap-2">
          <Button variant="ghost" onClick={() => setDeleteId(null)}>Cancel</Button>
          <Button variant="destructive" onClick={handleDelete}>Delete</Button>
        </div>
      </Modal>
    </div>
  )
}
