import { useState } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { collectionsApi } from '@/api/collections'
import { useAuth } from '@/hooks/use-auth'
import { RecipeCard } from '@/components/ui/recipe-card'
import { VisibilityBadge } from '@/components/ui/visibility-badge'
import { DeleteCollectionDialog } from '@/components/ui/delete-collection-dialog'
import { Pagination } from '@/components/ui/pagination'
import { Button } from '@/components/ui/button'
import { LoadingSpinner } from '@/components/ui/loading-spinner'
import { ErrorComponent } from '@/components/ui/error-component'
import { EmptyCollection } from '@/components/ui/empty-collection'
import { ArrowLeft, Edit, Trash2, Calendar, BookOpen, User } from 'lucide-react'
import toast from 'react-hot-toast'

export default function CollectionDetailPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { user } = useAuth()
  const collectionId = Number(id)
  const [recipePage, setRecipePage] = useState(0)
  const [showDelete, setShowDelete] = useState(false)

  const { data, isLoading, isError, refetch } = useQuery({
    queryKey: ['collection', collectionId],
    queryFn: () => collectionsApi.getById(collectionId),
    enabled: !!collectionId,
  })

  const recipesQuery = useQuery({
    queryKey: ['collection', collectionId, 'recipes', recipePage],
    queryFn: () => collectionsApi.getCollectionRecipes(collectionId, { page: recipePage, size: 20 }),
    enabled: !!collectionId,
  })

  const deleteMutation = useMutation({
    mutationFn: () => collectionsApi.delete(collectionId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['my-collections'] })
      queryClient.invalidateQueries({ queryKey: ['public-collections'] })
      toast.success('Collection deleted')
      navigate('/my-collections')
    },
    onError: (err: any) => {
      toast.error(err?.response?.data?.message || 'Failed to delete collection')
    },
  })

  const removeMutation = useMutation({
    mutationFn: (recipeId: number) => collectionsApi.removeRecipe(collectionId, recipeId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['collection', collectionId] })
      toast.success('Recipe removed from collection')
    },
    onError: (err: any) => {
      toast.error(err?.response?.data?.message || 'Failed to remove recipe')
    },
  })

  const collection = data?.data
  const isOwner = user && collection?.ownerUsername === user.username
  const createdAt = collection ? new Date(collection.createdAt).toLocaleDateString() : ''

  if (isLoading) return <LoadingSpinner />
  if (isError) return <ErrorComponent message="Failed to load collection" onRetry={() => refetch()} />
  if (!collection) return <ErrorComponent message="Collection not found" />

  const recipeData = recipesQuery.data?.data
  const recipeList = recipeData?.content || []

  return (
    <div className="space-y-6">
      <Link to="/collections" className="inline-flex items-center gap-1 text-sm text-[var(--muted-foreground)] hover:text-[var(--foreground)]">
        <ArrowLeft size={16} />
        Back to Collections
      </Link>

      <div className="flex flex-wrap items-start justify-between gap-4">
        <div className="space-y-2">
          <div className="flex items-center gap-3">
            <h1 className="text-2xl font-bold">{collection.name}</h1>
            <VisibilityBadge visibility={collection.visibility} />
          </div>
          {collection.description && (
            <p className="text-[var(--muted-foreground)]">{collection.description}</p>
          )}
          <div className="flex flex-wrap items-center gap-4 text-sm text-[var(--muted-foreground)]">
            <span className="flex items-center gap-1"><User size={14} /> {collection.ownerUsername}</span>
            <span className="flex items-center gap-1"><BookOpen size={14} /> {collection.totalRecipeCount} recipes</span>
            <span className="flex items-center gap-1"><Calendar size={14} /> Created {createdAt}</span>
          </div>
        </div>

        {isOwner && (
          <div className="flex items-center gap-2">
            <Link to={`/my-collections/${collectionId}/edit`}>
              <Button variant="outline" size="sm">
                <Edit size={16} />
                Edit
              </Button>
            </Link>
            <Button variant="destructive" size="sm" onClick={() => setShowDelete(true)}>
              <Trash2 size={16} />
              Delete
            </Button>
          </div>
        )}
      </div>

      <div>
        <h2 className="text-xl font-semibold mb-4">Recipes</h2>
        {recipesQuery.isPending ? (
          <LoadingSpinner />
        ) : recipesQuery.isError ? (
          <ErrorComponent message="Failed to load recipes" onRetry={() => recipesQuery.refetch()} />
        ) : recipeList.length === 0 ? (
          <EmptyCollection message="No recipes in this collection yet" />
        ) : (
          <>
            <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
              {recipeList.map((cr) => (
                <div key={cr.id} className="relative group">
                  <Link to={`/recipes/${cr.recipeId}`}>
                    <RecipeCard recipe={{
                      id: cr.recipeId,
                      title: cr.recipeTitle,
                      imageUrl: cr.recipeImageUrl,
                      categoryName: '',
                    }} />
                  </Link>
                  {isOwner && (
                    <button
                      onClick={() => removeMutation.mutate(cr.recipeId)}
                      className="absolute top-2 right-2 rounded-full bg-[var(--background)] p-1.5 shadow opacity-0 group-hover:opacity-100 transition-opacity hover:bg-red-50 hover:text-red-500"
                      title="Remove from collection"
                    >
                      <Trash2 size={14} />
                    </button>
                  )}
                </div>
              ))}
            </div>
            {recipeData && recipeData.totalPages > 1 && (
              <div className="mt-6">
                <Pagination page={recipeData.number} totalPages={recipeData.totalPages} onPageChange={setRecipePage} />
              </div>
            )}
          </>
        )}
      </div>

      <DeleteCollectionDialog
        collectionName={collection.name}
        isOpen={showDelete}
        onClose={() => setShowDelete(false)}
        onConfirm={() => deleteMutation.mutate()}
        isLoading={deleteMutation.isPending}
      />
    </div>
  )
}
