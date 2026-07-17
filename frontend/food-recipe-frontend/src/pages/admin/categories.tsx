import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { adminApi } from '@/api/admin'
import { DataTable } from '@/components/ui/data-table'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Modal } from '@/components/ui/modal'
import { LoadingSpinner } from '@/components/ui/loading-spinner'
import { ErrorComponent } from '@/components/ui/error-component'
import toast from 'react-hot-toast'
import { Edit3, Trash2, Plus } from 'lucide-react'
import type { CategoryResponse } from '@/types'

export default function AdminCategoriesPage() {
  const queryClient = useQueryClient()
  const [modalOpen, setModalOpen] = useState(false)
  const [editingCategory, setEditingCategory] = useState<CategoryResponse | null>(null)
  const [deleteTarget, setDeleteTarget] = useState<CategoryResponse | null>(null)
  const [name, setName] = useState('')

  const { data, isLoading, isError, refetch } = useQuery({
    queryKey: ['admin', 'categories'],
    queryFn: async () => {
      const res = await adminApi.getCategories()
      return res.data
    },
  })

  const createMutation = useMutation({
    mutationFn: async (name: string) => {
      const res = await adminApi.createCategory(name)
      return res.data
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'categories'] })
      toast.success('Category created')
      closeModal()
    },
    onError: (err: any) => {
      toast.error(err?.response?.data?.message || 'Failed to create category')
    },
  })

  const updateMutation = useMutation({
    mutationFn: async ({ id, name }: { id: number; name: string }) => {
      const res = await adminApi.updateCategory(id, name)
      return res.data
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'categories'] })
      toast.success('Category updated')
      closeModal()
    },
    onError: (err: any) => {
      toast.error(err?.response?.data?.message || 'Failed to update category')
    },
  })

  const deleteMutation = useMutation({
    mutationFn: async (id: number) => {
      await adminApi.deleteCategory(id)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'categories'] })
      toast.success('Category deleted')
      setDeleteTarget(null)
    },
    onError: (err: any) => {
      if (err?.response?.status === 409) {
        toast.error('Cannot delete category: it has associated recipes')
      } else {
        toast.error(err?.response?.data?.message || 'Failed to delete category')
      }
    },
  })

  const openCreateModal = () => {
    setEditingCategory(null)
    setName('')
    setModalOpen(true)
  }

  const openEditModal = (cat: CategoryResponse) => {
    setEditingCategory(cat)
    setName(cat.name)
    setModalOpen(true)
  }

  const closeModal = () => {
    setModalOpen(false)
    setEditingCategory(null)
    setName('')
  }

  const handleSubmit = () => {
    if (!name.trim()) {
      toast.error('Name is required')
      return
    }
    if (editingCategory) {
      updateMutation.mutate({ id: editingCategory.id, name: name.trim() })
    } else {
      createMutation.mutate(name.trim())
    }
  }

  const isMutating = createMutation.isPending || updateMutation.isPending

  if (isLoading) return <div className="p-6"><LoadingSpinner /></div>
  if (isError) return <div className="p-6"><ErrorComponent message="Failed to load categories" onRetry={() => refetch()} /></div>

  return (
    <div className="space-y-6 p-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">Categories</h1>
          <p className="text-[var(--muted-foreground)]">Manage recipe categories</p>
        </div>
        <Button onClick={openCreateModal}>
          <Plus size={16} />
          Add Category
        </Button>
      </div>

      <DataTable
        columns={[
          { key: 'id', label: 'ID' },
          { key: 'name', label: 'Name' },
          {
            key: 'actions',
            label: 'Actions',
            render: (item: CategoryResponse) => (
              <div className="flex gap-2">
                <Button variant="outline" size="sm" onClick={() => openEditModal(item)}>
                  <Edit3 size={14} />
                </Button>
                <Button variant="destructive" size="sm" onClick={() => setDeleteTarget(item)}>
                  <Trash2 size={14} />
                </Button>
              </div>
            ),
          },
        ]}
        data={data || []}
        keyExtractor={(item) => item.id}
      />

      <Modal
        isOpen={modalOpen}
        onClose={closeModal}
        title={editingCategory ? 'Edit Category' : 'Create Category'}
      >
        <div className="space-y-4">
          <Input
            label="Name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSubmit()}
            placeholder="Category name"
          />
          <div className="flex justify-end gap-2">
            <Button variant="outline" onClick={closeModal}>
              Cancel
            </Button>
            <Button onClick={handleSubmit} isLoading={isMutating}>
              {editingCategory ? 'Update' : 'Create'}
            </Button>
          </div>
        </div>
      </Modal>

      <Modal
        isOpen={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        title="Delete Category"
      >
        <p className="mb-4 text-sm">
          Are you sure you want to delete <strong>{deleteTarget?.name}</strong>?
        </p>
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
