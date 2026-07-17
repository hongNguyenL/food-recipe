import { useParams, useNavigate } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { collectionsApi } from '@/api/collections'
import { CollectionForm, type CollectionFormData } from '@/components/ui/collection-form'
import { LoadingSpinner } from '@/components/ui/loading-spinner'
import { ErrorComponent } from '@/components/ui/error-component'
import toast from 'react-hot-toast'

export default function EditCollectionPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const collectionId = Number(id)

  const { data, isLoading, isError, refetch } = useQuery({
    queryKey: ['collection', collectionId],
    queryFn: () => collectionsApi.getById(collectionId),
    enabled: !!collectionId,
  })

  const mutation = useMutation({
    mutationFn: (formData: CollectionFormData) => collectionsApi.update(collectionId, {
      name: formData.name,
      description: formData.description || '',
      visibility: formData.visibility,
    }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['collection', collectionId] })
      queryClient.invalidateQueries({ queryKey: ['my-collections'] })
      toast.success('Collection updated')
      navigate(`/collections/${collectionId}`)
    },
    onError: (err: any) => {
      toast.error(err?.response?.data?.message || 'Failed to update collection')
    },
  })

  if (isLoading) return <LoadingSpinner />
  if (isError) return <ErrorComponent message="Failed to load collection" onRetry={() => refetch()} />
  if (!data?.data) return <ErrorComponent message="Collection not found" />

  const collection = data.data

  return (
    <div className="mx-auto max-w-lg space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Edit Collection</h1>
        <p className="text-[var(--muted-foreground)]">Update your collection details</p>
      </div>

      <CollectionForm
        defaultValues={{
          name: collection.name,
          description: collection.description,
          visibility: collection.visibility,
        }}
        onSubmit={(formData) => mutation.mutate(formData)}
        isSubmitting={mutation.isPending}
        submitLabel="Save Changes"
      />
    </div>
  )
}
