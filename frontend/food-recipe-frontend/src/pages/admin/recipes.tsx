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
import { useForm, useFieldArray } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import toast from 'react-hot-toast'
import { Edit3, Trash2, Plus, Search } from 'lucide-react'
import type { Page, RecipeSummary, RecipeFormData } from '@/types'

const recipeSchema = z.object({
  title: z.string().min(1, 'Title is required'),
  imageUrl: z.string().min(1, 'Image URL is required'),
  description: z.string().min(1, 'Description is required'),
  categoryId: z.number().min(1, 'Category is required'),
  ingredients: z.array(z.string().min(1, 'Ingredient cannot be empty')).min(1, 'At least one ingredient'),
  instructions: z
    .array(
      z.object({
        stepNumber: z.number(),
        instructionText: z.string().min(1, 'Instruction cannot be empty'),
      })
    )
    .min(1, 'At least one instruction'),
})

type RecipeFormValues = z.infer<typeof recipeSchema>

export default function AdminRecipesPage() {
  const queryClient = useQueryClient()
  const [searchParams, setSearchParams] = useState(() => new URLSearchParams())
  const keyword = searchParams.get('keyword') || ''
  const page = parseInt(searchParams.get('page') || '0', 10)
  const [searchInput, setSearchInput] = useState(keyword)

  const [modalOpen, setModalOpen] = useState(false)
  const [editingRecipe, setEditingRecipe] = useState<RecipeSummary | null>(null)
  const [deleteTarget, setDeleteTarget] = useState<RecipeSummary | null>(null)
  const [categories, setCategories] = useState<{ value: string; label: string }[]>([])

  useQuery({
    queryKey: ['admin', 'categories'],
    queryFn: async () => {
      const res = await adminApi.getCategories()
      setCategories(res.data.map((c) => ({ value: String(c.id), label: c.name })))
      return res.data
    },
  })

  const { data, isLoading, isError, refetch } = useQuery({
    queryKey: ['admin', 'recipes', { keyword, page }],
    queryFn: async (): Promise<Page<RecipeSummary>> => {
      const res = await adminApi.getRecipes({ keyword: keyword || undefined, page, size: 10 })
      return res.data
    },
  })

  const form = useForm<RecipeFormValues>({
    resolver: zodResolver(recipeSchema),
    defaultValues: {
      title: '',
      imageUrl: '',
      description: '',
      categoryId: 0,
      ingredients: [''],
      instructions: [{ stepNumber: 1, instructionText: '' }],
    },
  })

  const ingFieldArray = useFieldArray({ control: form.control as any, name: 'ingredients' })
  const instFieldArray = useFieldArray({ control: form.control as any, name: 'instructions' })
  const ingredientFields = ingFieldArray.fields
  const appendIng = ingFieldArray.append
  const removeIng = ingFieldArray.remove
  const instructionFields = instFieldArray.fields
  const appendInst = instFieldArray.append
  const removeInst = instFieldArray.remove

  const createMutation = useMutation({
    mutationFn: async (data: RecipeFormData) => {
      const res = await adminApi.createRecipe(data)
      return res.data
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'recipes'] })
      toast.success('Recipe created')
      closeModal()
    },
    onError: (err: any) => {
      toast.error(err?.response?.data?.message || 'Failed to create recipe')
    },
  })

  const updateMutation = useMutation({
    mutationFn: async ({ id, data }: { id: number; data: RecipeFormData }) => {
      const res = await adminApi.updateRecipe(id, data)
      return res.data
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'recipes'] })
      toast.success('Recipe updated')
      closeModal()
    },
    onError: (err: any) => {
      toast.error(err?.response?.data?.message || 'Failed to update recipe')
    },
  })

  const deleteMutation = useMutation({
    mutationFn: async (id: number) => {
      await adminApi.deleteRecipe(id)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'recipes'] })
      toast.success('Recipe deleted')
      setDeleteTarget(null)
    },
    onError: (err: any) => {
      toast.error(err?.response?.data?.message || 'Failed to delete recipe')
    },
  })

  const openCreateModal = () => {
    setEditingRecipe(null)
    form.reset({
      title: '',
      imageUrl: '',
      description: '',
      categoryId: 0,
      ingredients: [''],
      instructions: [{ stepNumber: 1, instructionText: '' }],
    })
    setModalOpen(true)
  }

  const openEditModal = async (recipe: RecipeSummary) => {
    setEditingRecipe(recipe)
    try {
      const res = await adminApi.getRecipe(recipe.id)
      const detail = res.data
      form.reset({
        title: detail.title,
        imageUrl: detail.imageUrl,
        description: detail.description,
        categoryId: detail.category.id,
        ingredients: detail.ingredients.map((i) => i.ingredientText),
        instructions: detail.instructions
          .sort((a, b) => a.stepNumber - b.stepNumber)
          .map((i) => ({ stepNumber: i.stepNumber, instructionText: i.instructionText })),
      })
    } catch {
      toast.error('Failed to load recipe details')
      return
    }
    setModalOpen(true)
  }

  const closeModal = () => {
    setModalOpen(false)
    setEditingRecipe(null)
  }

  const onSubmit = (values: RecipeFormValues) => {
    const payload: RecipeFormData = {
      title: values.title,
      imageUrl: values.imageUrl,
      description: values.description,
      categoryId: values.categoryId,
      ingredients: values.ingredients,
      instructions: values.instructions.map((inst, idx) => ({
        stepNumber: idx + 1,
        instructionText: inst.instructionText,
      })),
    }
    if (editingRecipe) {
      updateMutation.mutate({ id: editingRecipe.id, data: payload })
    } else {
      createMutation.mutate(payload)
    }
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

  const renderActions = (item: RecipeSummary) => (
    <div className="flex gap-2">
      <Button variant="outline" size="sm" onClick={() => openEditModal(item)}>
        <Edit3 size={14} />
      </Button>
      <Button variant="destructive" size="sm" onClick={() => setDeleteTarget(item)}>
        <Trash2 size={14} />
      </Button>
    </div>
  )

  const isMutating = createMutation.isPending || updateMutation.isPending

  const categoryOptions = categories.map((c) => ({
    value: c.value,
    label: c.label,
  }))

  return (
    <div className="space-y-6 p-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">Recipes</h1>
          <p className="text-[var(--muted-foreground)]">Manage all recipes</p>
        </div>
        <Button onClick={openCreateModal}>
          <Plus size={16} />
          Add Recipe
        </Button>
      </div>

      <div className="flex gap-2">
        <Input
          value={searchInput}
          onChange={(e) => setSearchInput(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
          placeholder="Search recipes..."
          className="max-w-xs"
        />
        <Button variant="outline" onClick={handleSearch}>
          <Search size={16} />
        </Button>
      </div>

      {isLoading ? (
        <LoadingSpinner />
      ) : isError ? (
        <ErrorComponent message="Failed to load recipes" onRetry={() => refetch()} />
      ) : data ? (
        <>
          <DataTable
            columns={[
              { key: 'id', label: 'ID' },
              { key: 'title', label: 'Title' },
              {
                key: 'imageUrl',
                label: 'Image',
                render: (item: RecipeSummary) =>
                  item.imageUrl ? (
                    <img
                      src={item.imageUrl}
                      alt={item.title}
                      className="h-10 w-14 rounded object-cover"
                    />
                  ) : (
                    <span className="text-[var(--muted-foreground)]">—</span>
                  ),
              },
              { key: 'categoryName', label: 'Category' },
              {
                key: 'actions',
                label: 'Actions',
                render: renderActions,
              },
            ]}
            data={data.content}
            keyExtractor={(item) => item.id}
          />
          <Pagination
            page={data.page}
            totalPages={data.totalPages}
            onPageChange={handlePageChange}
          />
        </>
      ) : null}

      <Modal
        isOpen={modalOpen}
        onClose={closeModal}
        title={editingRecipe ? 'Edit Recipe' : 'Create Recipe'}
      >
        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
          <Input
            label="Title"
            {...form.register('title')}
            error={form.formState.errors.title?.message}
          />
          <Input
            label="Image URL"
            {...form.register('imageUrl')}
            error={form.formState.errors.imageUrl?.message}
          />
          <div className="space-y-1">
            <label className="text-sm font-medium">Description</label>
            <textarea
              className="flex h-20 w-full rounded-md border border-[var(--input)] bg-[var(--background)] px-3 py-2 text-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[var(--ring)]"
              {...form.register('description')}
            />
            {form.formState.errors.description && (
              <p className="text-sm text-red-500">{form.formState.errors.description.message}</p>
            )}
          </div>
          <div className="space-y-1">
            <label className="text-sm font-medium">Category</label>
            <select
              className="flex h-10 w-full rounded-md border border-[var(--input)] bg-[var(--background)] px-3 py-2 text-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[var(--ring)]"
              value={form.watch('categoryId') ? String(form.watch('categoryId')) : ''}
              onChange={(e) => form.setValue('categoryId', e.target.value ? Number(e.target.value) : 0)}
            >
              <option value="">Select category</option>
              {categories.map((c) => (
                <option key={c.value} value={c.value}>{c.label}</option>
              ))}
            </select>
            {form.formState.errors.categoryId && (
              <p className="text-sm text-red-500">{form.formState.errors.categoryId.message}</p>
            )}
          </div>

          <div className="space-y-2">
            <label className="text-sm font-medium">Ingredients</label>
            {ingredientFields.map((field, index) => (
              <div key={field.id} className="flex gap-2 items-start">
                <Input
                  {...form.register(`ingredients.${index}`)}
                  placeholder={`Ingredient ${index + 1}`}
                  error={form.formState.errors.ingredients?.[index]?.message}
                />
                {ingredientFields.length > 1 && (
                  <Button type="button" variant="ghost" size="sm" onClick={() => removeIng(index)}>
                    &times;
                  </Button>
                )}
              </div>
            ))}
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={() => appendIng('')}
            >
              <Plus size={14} /> Add Ingredient
            </Button>
            {form.formState.errors.ingredients?.message && (
              <p className="text-sm text-red-500">{form.formState.errors.ingredients.message}</p>
            )}
          </div>

          <div className="space-y-2">
            <label className="text-sm font-medium">Instructions</label>
            {instructionFields.map((field, index) => (
              <div key={field.id} className="flex gap-2 items-start">
                <span className="mt-2.5 text-sm font-medium w-6">{index + 1}.</span>
                <div className="flex-1">
                  <textarea
                    className="flex h-16 w-full rounded-md border border-[var(--input)] bg-[var(--background)] px-3 py-2 text-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[var(--ring)]"
                    {...form.register(`instructions.${index}.instructionText`)}
                    placeholder={`Step ${index + 1}`}
                  />
                  {form.formState.errors.instructions?.[index]?.instructionText && (
                    <p className="text-sm text-red-500">
                      {form.formState.errors.instructions[index]?.instructionText?.message}
                    </p>
                  )}
                </div>
                {instructionFields.length > 1 && (
                  <Button type="button" variant="ghost" size="sm" onClick={() => removeInst(index)}>
                    &times;
                  </Button>
                )}
              </div>
            ))}
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={() => appendInst({ stepNumber: instructionFields.length + 1, instructionText: '' })}
            >
              <Plus size={14} /> Add Step
            </Button>
            {form.formState.errors.instructions?.message && (
              <p className="text-sm text-red-500">{form.formState.errors.instructions.message}</p>
            )}
          </div>

          <div className="flex justify-end gap-2 pt-2">
            <Button type="button" variant="outline" onClick={closeModal}>
              Cancel
            </Button>
            <Button type="submit" isLoading={isMutating}>
              {editingRecipe ? 'Update' : 'Create'}
            </Button>
          </div>
        </form>
      </Modal>

      <Modal
        isOpen={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        title="Delete Recipe"
      >
        <p className="mb-4 text-sm">
          Are you sure you want to delete <strong>{deleteTarget?.title}</strong>? This action cannot be undone.
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
