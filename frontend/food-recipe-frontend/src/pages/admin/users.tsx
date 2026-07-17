import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { adminApi } from '@/api/admin'
import { DataTable } from '@/components/ui/data-table'
import { Pagination } from '@/components/ui/pagination'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Badge } from '@/components/ui/badge'
import { Modal } from '@/components/ui/modal'
import { LoadingSpinner } from '@/components/ui/loading-spinner'
import { ErrorComponent } from '@/components/ui/error-component'
import toast from 'react-hot-toast'
import { Edit3, Search, ToggleLeft, ToggleRight, Shield } from 'lucide-react'
import type { AdminUserResponse, Page } from '@/types'

const ROLE_OPTIONS = ['USER', 'ADMIN'] as const

export default function AdminUsersPage() {
  const queryClient = useQueryClient()
  const [searchParams, setSearchParams] = useState(() => new URLSearchParams())
  const keyword = searchParams.get('keyword') || ''
  const page = parseInt(searchParams.get('page') || '0', 10)
  const [searchInput, setSearchInput] = useState(keyword)

  const [editModalOpen, setEditModalOpen] = useState(false)
  const [editingUser, setEditingUser] = useState<AdminUserResponse | null>(null)
  const [editUsername, setEditUsername] = useState('')
  const [editEmail, setEditEmail] = useState('')

  const [roleModalOpen, setRoleModalOpen] = useState(false)
  const [roleTarget, setRoleTarget] = useState<AdminUserResponse | null>(null)
  const [selectedRole, setSelectedRole] = useState<'USER' | 'ADMIN'>('USER')

  const { data, isLoading, isError, refetch } = useQuery({
    queryKey: ['admin', 'users', { keyword, page }],
    queryFn: async () => {
      const res = await adminApi.getUsers({ keyword: keyword || undefined, page, size: 10 })
      return res.data as Page<AdminUserResponse>
    },
  })

  const enableMutation = useMutation({
    mutationFn: async (id: number) => {
      await adminApi.enableUser(id)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'users'] })
      toast.success('User enabled')
    },
    onError: (err: any) => {
      toast.error(err?.response?.data?.message || 'Failed to enable user')
    },
  })

  const disableMutation = useMutation({
    mutationFn: async (id: number) => {
      await adminApi.disableUser(id)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'users'] })
      toast.success('User disabled')
    },
    onError: (err: any) => {
      toast.error(err?.response?.data?.message || 'Failed to disable user')
    },
  })

  const updateMutation = useMutation({
    mutationFn: async ({ id, data }: { id: number; data: { username?: string; email?: string } }) => {
      const res = await adminApi.updateUser(id, data)
      return res.data
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'users'] })
      toast.success('User updated')
      setEditModalOpen(false)
      setEditingUser(null)
    },
    onError: (err: any) => {
      toast.error(err?.response?.data?.message || 'Failed to update user')
    },
  })

  const roleMutation = useMutation({
    mutationFn: async ({ id, role }: { id: number; role: 'USER' | 'ADMIN' }) => {
      await adminApi.changeUserRole(id, role)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'users'] })
      toast.success('Role updated')
      setRoleModalOpen(false)
      setRoleTarget(null)
    },
    onError: (err: any) => {
      toast.error(err?.response?.data?.message || 'Failed to change role')
    },
  })

  const openEditModal = (user: AdminUserResponse) => {
    setEditingUser(user)
    setEditUsername(user.username)
    setEditEmail(user.email)
    setEditModalOpen(true)
  }

  const openRoleModal = (user: AdminUserResponse) => {
    setRoleTarget(user)
    setSelectedRole(user.role)
    setRoleModalOpen(true)
  }

  const handleEditSubmit = () => {
    if (!editingUser) return
    const payload: { username?: string; email?: string } = {}
    if (editUsername !== editingUser.username) payload.username = editUsername
    if (editEmail !== editingUser.email) payload.email = editEmail
    if (Object.keys(payload).length === 0) {
      setEditModalOpen(false)
      return
    }
    updateMutation.mutate({ id: editingUser.id, data: payload })
  }

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
        <h1 className="text-2xl font-bold">Users</h1>
        <p className="text-[var(--muted-foreground)]">Manage registered users</p>
      </div>

      <div className="flex gap-2">
        <Input
          value={searchInput}
          onChange={(e) => setSearchInput(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
          placeholder="Search users..."
          className="max-w-xs"
        />
        <Button variant="outline" onClick={handleSearch}>
          <Search size={16} />
        </Button>
      </div>

      {isLoading ? (
        <LoadingSpinner />
      ) : isError ? (
        <ErrorComponent message="Failed to load users" onRetry={() => refetch()} />
      ) : data ? (
        <>
          <DataTable
            columns={[
              { key: 'id', label: 'ID' },
              { key: 'username', label: 'Username' },
              { key: 'email', label: 'Email' },
              {
                key: 'role',
                label: 'Role',
                render: (item: AdminUserResponse) => (
                  <Badge variant={item.role === 'ADMIN' ? 'default' : 'secondary'}>
                    {item.role}
                  </Badge>
                ),
              },
              {
                key: 'enabled',
                label: 'Enabled',
                render: (item: AdminUserResponse) => (
                  <Badge variant={item.enabled ? 'default' : 'outline'}>
                    {item.enabled ? 'Yes' : 'No'}
                  </Badge>
                ),
              },
              {
                key: 'createdAt',
                label: 'Created At',
                render: (item: AdminUserResponse) =>
                  new Date(item.createdAt).toLocaleDateString(),
              },
              {
                key: 'actions',
                label: 'Actions',
                render: (item: AdminUserResponse) => (
                  <div className="flex gap-2">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() =>
                        item.enabled
                          ? disableMutation.mutate(item.id)
                          : enableMutation.mutate(item.id)
                      }
                      isLoading={
                        (enableMutation.isPending || disableMutation.isPending) &&
                        ((enableMutation.isPending && !item.enabled) ||
                          (disableMutation.isPending && item.enabled))
                      }
                      title={item.enabled ? 'Disable' : 'Enable'}
                    >
                      {item.enabled ? <ToggleRight size={14} /> : <ToggleLeft size={14} />}
                    </Button>
                    <Button variant="outline" size="sm" onClick={() => openRoleModal(item)}>
                      <Shield size={14} />
                    </Button>
                    <Button variant="outline" size="sm" onClick={() => openEditModal(item)}>
                      <Edit3 size={14} />
                    </Button>
                  </div>
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
        isOpen={editModalOpen}
        onClose={() => setEditModalOpen(false)}
        title="Edit User"
      >
        <div className="space-y-4">
          <Input
            label="Username"
            value={editUsername}
            onChange={(e) => setEditUsername(e.target.value)}
          />
          <Input
            label="Email"
            value={editEmail}
            onChange={(e) => setEditEmail(e.target.value)}
          />
          <div className="flex justify-end gap-2">
            <Button variant="outline" onClick={() => setEditModalOpen(false)}>
              Cancel
            </Button>
            <Button onClick={handleEditSubmit} isLoading={updateMutation.isPending}>
              Save
            </Button>
          </div>
        </div>
      </Modal>

      <Modal
        isOpen={roleModalOpen}
        onClose={() => setRoleModalOpen(false)}
        title="Change Role"
      >
        <div className="space-y-4">
          <p className="text-sm">
            Change role for <strong>{roleTarget?.username}</strong>
          </p>
          <div className="flex gap-4">
            {ROLE_OPTIONS.map((r) => (
              <label key={r} className="flex items-center gap-2 cursor-pointer">
                <input
                  type="radio"
                  name="role"
                  value={r}
                  checked={selectedRole === r}
                  onChange={() => setSelectedRole(r)}
                  className="accent-[var(--primary)]"
                />
                {r}
              </label>
            ))}
          </div>
          <div className="flex justify-end gap-2">
            <Button variant="outline" onClick={() => setRoleModalOpen(false)}>
              Cancel
            </Button>
            <Button
              onClick={() =>
                roleTarget && roleMutation.mutate({ id: roleTarget.id, role: selectedRole })
              }
              isLoading={roleMutation.isPending}
            >
              Change
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  )
}
