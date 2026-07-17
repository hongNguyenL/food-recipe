import { useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { recipesApi } from '@/api/recipes'
import { commentsApi } from '@/api/comments'
import { useAuth } from '@/hooks/use-auth'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Textarea } from '@/components/ui/textarea'
import { RatingStars } from '@/components/ui/rating-stars'
import { FavoriteButton } from '@/components/ui/favorite-button'
import { CommentList } from '@/components/ui/comment-list'
import { LoadingSpinner } from '@/components/ui/loading-spinner'
import { ErrorComponent } from '@/components/ui/error-component'
import { RecipeCard } from '@/components/ui/recipe-card'
import toast from 'react-hot-toast'

export default function RecipeDetail() {
  const { id } = useParams<{ id: string }>()
  const recipeId = Number(id)
  const queryClient = useQueryClient()
  const auth = useAuth()
  const [commentPage, setCommentPage] = useState(0)
  const [newComment, setNewComment] = useState('')
  const [isFavorited, setIsFavorited] = useState(false)

  const detailQuery = useQuery({
    queryKey: ['recipe', recipeId],
    queryFn: () => recipesApi.getById(recipeId),
    enabled: !!recipeId,
  })

  const ratingQuery = useQuery({
    queryKey: ['recipe', recipeId, 'rating'],
    queryFn: () => recipesApi.getRating(recipeId),
    enabled: !!recipeId,
  })

  const commentsQuery = useQuery({
    queryKey: ['recipe', recipeId, 'comments', commentPage],
    queryFn: () => recipesApi.getComments(recipeId, { page: commentPage, size: 10 }),
    enabled: !!recipeId,
  })

  const similarQuery = useQuery({
    queryKey: ['recipe', recipeId, 'similar'],
    queryFn: () => recipesApi.similar(recipeId),
    enabled: !!recipeId,
  })

  const rateMutation = useMutation({
    mutationFn: (rating: number) => recipesApi.rate(recipeId, rating),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['recipe', recipeId, 'rating'] })
      toast.success('Rating submitted')
    },
    onError: (err: any) => {
      toast.error(err?.response?.data?.message || 'Failed to submit rating')
    },
  })

  const favoriteMutation = useMutation({
    mutationFn: () => isFavorited ? recipesApi.unfavorite(recipeId) : recipesApi.favorite(recipeId),
    onSuccess: () => {
      setIsFavorited((prev) => !prev)
      queryClient.invalidateQueries({ queryKey: ['recipe', recipeId] })
      toast.success(isFavorited ? 'Removed from favorites' : 'Added to favorites')
    },
    onError: (err: any) => {
      toast.error(err?.response?.data?.message || 'Failed to update favorite')
    },
  })

  const commentMutation = useMutation({
    mutationFn: (content: string) => recipesApi.addComment(recipeId, content),
    onSuccess: () => {
      setNewComment('')
      queryClient.invalidateQueries({ queryKey: ['recipe', recipeId, 'comments'] })
      toast.success('Comment added')
    },
    onError: (err: any) => {
      toast.error(err?.response?.data?.message || 'Failed to add comment')
    },
  })

  const handleEditComment = async (commentId: number, content: string) => {
    await commentsApi.update(commentId, content)
    queryClient.invalidateQueries({ queryKey: ['recipe', recipeId, 'comments'] })
  }

  const handleDeleteComment = async (commentId: number) => {
    await commentsApi.delete(commentId)
    queryClient.invalidateQueries({ queryKey: ['recipe', recipeId, 'comments'] })
  }

  if (!recipeId) {
    return <ErrorComponent message="Invalid recipe ID" />
  }

  if (detailQuery.isPending) {
    return <LoadingSpinner />
  }

  if (detailQuery.isError) {
    return <ErrorComponent message="Failed to load recipe" onRetry={() => detailQuery.refetch()} />
  }

  const recipe = detailQuery.data.data
  const rating = ratingQuery.data?.data
  const comments = commentsQuery.data?.data
  const similar = similarQuery.data?.data

  const sortedInstructions = [...recipe.instructions].sort((a, b) => a.stepNumber - b.stepNumber)

  return (
    <div className="grid gap-8 lg:grid-cols-3">
      <div className="lg:col-span-2 space-y-8">
        <div className="overflow-hidden rounded-lg">
          {recipe.imageUrl ? (
            <img
              src={recipe.imageUrl}
              alt={recipe.title}
              className="h-80 w-full object-cover"
              referrerPolicy="no-referrer"
              onError={(e) => { (e.target as HTMLImageElement).style.display = 'none' }}
            />
          ) : (
            <div className="flex h-80 items-center justify-center bg-[var(--muted)] text-[var(--muted-foreground)]">
              No Image
            </div>
          )}
        </div>

        <div>
          <div className="flex items-start justify-between gap-4">
            <div>
              <h1 className="text-3xl font-bold">{recipe.title}</h1>
              <div className="mt-2 flex items-center gap-3">
                <Badge variant="secondary">{recipe.category.name}</Badge>
                {rating && (
                  <div className="flex items-center gap-1 text-sm text-[var(--muted-foreground)]">
                    <RatingStars rating={rating.averageRating} />
                    <span>({rating.totalRatings})</span>
                  </div>
                )}
                <span className="text-sm text-[var(--muted-foreground)]">
                  {recipe.favoriteCount} favorites
                </span>
                <span className="text-sm text-[var(--muted-foreground)]">
                  {recipe.totalComments} comments
                </span>
              </div>
            </div>
            {auth.isAuthenticated && (
              <FavoriteButton
                isFavorited={isFavorited}
                count={recipe.favoriteCount}
                onToggle={() => favoriteMutation.mutate()}
                isLoading={favoriteMutation.isPending}
              />
            )}
          </div>

          <p className="mt-4 text-[var(--muted-foreground)]">{recipe.description}</p>
        </div>

        {auth.isAuthenticated && rating && (
          <Card>
            <CardHeader>
              <CardTitle>Rate this Recipe</CardTitle>
            </CardHeader>
            <CardContent>
              <RatingStars
                rating={rating.currentUserRating || 0}
                interactive
                onChange={(value) => rateMutation.mutate(value)}
                size={28}
              />
            </CardContent>
          </Card>
        )}

        <Card>
          <CardHeader>
            <CardTitle>Ingredients</CardTitle>
          </CardHeader>
          <CardContent>
            <ul className="list-inside list-disc space-y-1">
              {recipe.ingredients.map((ing) => (
                <li key={ing.id} className="text-sm">{ing.ingredientText}</li>
              ))}
            </ul>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Instructions</CardTitle>
          </CardHeader>
          <CardContent>
            <ol className="space-y-4">
              {sortedInstructions.map((inst) => (
                <li key={inst.id} className="flex gap-3">
                  <span className="flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-[var(--primary)] text-xs text-[var(--primary-foreground)]">
                    {inst.stepNumber}
                  </span>
                  <p className="text-sm">{inst.instructionText}</p>
                </li>
              ))}
            </ol>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Comments ({recipe.totalComments})</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            {auth.isAuthenticated && (
              <div className="space-y-2">
                <Textarea
                  value={newComment}
                  onChange={(e) => setNewComment(e.target.value)}
                  placeholder="Write a comment..."
                  rows={3}
                />
                <Button
                  onClick={() => commentMutation.mutate(newComment)}
                  isLoading={commentMutation.isPending}
                  disabled={!newComment.trim()}
                >
                  Post Comment
                </Button>
              </div>
            )}

            {commentsQuery.isPending ? (
              <LoadingSpinner />
            ) : commentsQuery.isError ? (
              <ErrorComponent message="Failed to load comments" onRetry={() => commentsQuery.refetch()} />
            ) : !comments || comments.content.length === 0 ? (
              <p className="text-[var(--muted-foreground)]">No comments yet.</p>
            ) : (
              <CommentList
                comments={comments.content}
                currentUserId={auth.user?.id}
                isAdmin={auth.isAdmin}
                page={comments.number}
                totalPages={comments.totalPages}
                onPageChange={setCommentPage}
                onEdit={handleEditComment}
                onDelete={handleDeleteComment}
              />
            )}
          </CardContent>
        </Card>
      </div>

      <div className="space-y-6">
        <Card>
          <CardHeader>
            <CardTitle>Similar Recipes</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            {similarQuery.isPending ? (
              <LoadingSpinner />
            ) : similarQuery.isError ? (
              <ErrorComponent message="Failed to load similar recipes" />
            ) : !similar || similar.length === 0 ? (
              <p className="text-sm text-[var(--muted-foreground)]">No similar recipes found.</p>
            ) : (
              similar.map((s) => (
                <Link key={s.id} to={`/recipes/${s.id}`}>
                  <RecipeCard recipe={s} />
                </Link>
              ))
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
