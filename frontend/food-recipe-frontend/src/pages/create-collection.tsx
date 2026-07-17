import { useNavigate } from 'react-router-dom'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { collectionsApi } from '@/api/collections'
import { CollectionForm, type CollectionFormData } from '@/components/ui/collection-form'
import toast from 'react-hot-toast'

export default function CreateCollectionPage() {
  const navigate = useNavigate()
  const queryClient = useQueryClient()

  const mutation = useMutation({
    mutationFn: (data: CollectionFormData) => collectionsApi.create({
      name: data.name,
      description: data.description || '',
      visibility: data.visibility,
    }),
    onSuccess: (res) => {
      queryClient.invalidateQueries({ queryKey: ['my-collections'] })
      toast.success('Collection created')
      navigate(`/collections/${res.data.id}`)
    },
    onError: (err: any) => {
      toast.error(err?.response?.data?.message || 'Failed to create collection')
    },
  })

  return (
    <div className="mx-auto max-w-lg space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Create Collection</h1>
        <p className="text-[var(--muted-foreground)]">Organize your favorite recipes</p>
      </div>

      <CollectionForm
        onSubmit={(data) => mutation.mutate(data)}
        isSubmitting={mutation.isPending}
        submitLabel="Create Collection"
      />
    </div>
  )
}
