import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { Select } from '@/components/ui/select'
import { Button } from '@/components/ui/button'

const collectionSchema = z.object({
  name: z.string().min(1, 'Name is required').max(100, 'Name must not exceed 100 characters'),
  description: z.string().max(1000, 'Description must not exceed 1000 characters').optional().or(z.literal('')),
  visibility: z.enum(['PUBLIC', 'PRIVATE'], { required_error: 'Visibility is required' }),
})

export type CollectionFormData = z.infer<typeof collectionSchema>

interface CollectionFormProps {
  defaultValues?: Partial<CollectionFormData>
  onSubmit: (data: CollectionFormData) => void
  isSubmitting: boolean
  submitLabel?: string
}

export function CollectionForm({ defaultValues, onSubmit, isSubmitting, submitLabel = 'Create' }: CollectionFormProps) {
  const { register, handleSubmit, formState: { errors } } = useForm<CollectionFormData>({
    resolver: zodResolver(collectionSchema),
    defaultValues: {
      name: '',
      description: '',
      visibility: 'PUBLIC',
      ...defaultValues,
    },
  })

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      <Input
        label="Name"
        placeholder="Collection name"
        error={errors.name?.message}
        {...register('name')}
      />

      <Textarea
        label="Description"
        placeholder="Describe your collection (optional)"
        error={errors.description?.message}
        rows={4}
        {...register('description')}
      />

      <Select
        label="Visibility"
        error={errors.visibility?.message}
        options={[
          { value: 'PUBLIC', label: 'Public' },
          { value: 'PRIVATE', label: 'Private' },
        ]}
        {...register('visibility')}
      />

      <Button type="submit" isLoading={isSubmitting}>
        {submitLabel}
      </Button>
    </form>
  )
}
