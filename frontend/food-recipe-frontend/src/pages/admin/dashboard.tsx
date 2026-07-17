import { useQuery } from '@tanstack/react-query'
import { adminApi } from '@/api/admin'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { LoadingSpinner } from '@/components/ui/loading-spinner'
import { ErrorComponent } from '@/components/ui/error-component'
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from 'recharts'
import {
  UtensilsCrossed,
  Users,
  FolderTree,
  Heart,
  Star,
  MessageSquare,
  Award,
} from 'lucide-react'

const statCards = [
  { key: 'totalRecipes', label: 'Total Recipes', icon: UtensilsCrossed, color: 'text-blue-500' },
  { key: 'totalUsers', label: 'Total Users', icon: Users, color: 'text-green-500' },
  { key: 'totalCategories', label: 'Categories', icon: FolderTree, color: 'text-purple-500' },
  { key: 'totalFavorites', label: 'Favorites', icon: Heart, color: 'text-red-500' },
  { key: 'totalRatings', label: 'Ratings', icon: Star, color: 'text-yellow-500' },
  { key: 'totalComments', label: 'Comments', icon: MessageSquare, color: 'text-cyan-500' },
  { key: 'averageRating', label: 'Avg Rating', icon: Award, color: 'text-orange-500' },
]

export default function AdminDashboardPage() {
  const { data, isLoading, isError, refetch } = useQuery({
    queryKey: ['admin', 'dashboard'],
    queryFn: async () => {
      const res = await adminApi.getDashboard()
      return res.data
    },
  })

  if (isLoading) return <LoadingSpinner />
  if (isError) return <ErrorComponent message="Failed to load dashboard" onRetry={() => refetch()} />
  if (!data) return null

  const d = data as unknown as Record<string, unknown>
  const chartData = statCards.map((s) => ({
    name: s.label,
    value: typeof d[s.key] === 'number' ? Number(d[s.key]) : 0,
  }))

  return (
    <div className="space-y-6 p-6">
      <div>
        <h1 className="text-2xl font-bold">Admin Dashboard</h1>
        <p className="text-[var(--muted-foreground)]">Overview of your platform</p>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {statCards.map((s) => (
          <Card key={s.key}>
            <CardContent className="flex items-center gap-4 p-6">
              <div className={`rounded-full p-3 bg-[var(--muted)] ${s.color}`}>
                <s.icon size={24} />
              </div>
              <div>
                <p className="text-2xl font-bold">
                  {String(d[s.key] ?? '')}
                </p>
                <p className="text-sm text-[var(--muted-foreground)]">{s.label}</p>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card>
          <CardHeader>
            <CardTitle>Statistics Overview</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" />
                <XAxis dataKey="name" tick={{ fontSize: 12 }} />
                <YAxis />
                <Tooltip
                  contentStyle={{
                    backgroundColor: 'var(--card)',
                    border: '1px solid var(--border)',
                    borderRadius: '8px',
                  }}
                />
                <Bar dataKey="value" fill="var(--primary)" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        <div className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Newest Users</CardTitle>
            </CardHeader>
            <CardContent>
              {data.newestUsers.length === 0 ? (
                <p className="text-sm text-[var(--muted-foreground)]">No users yet.</p>
              ) : (
                <div className="space-y-3">
                  {data.newestUsers.map((u, i) => (
                    <div key={i} className="flex items-center justify-between text-sm">
                      <span className="font-medium">{u.username}</span>
                      <span className="text-[var(--muted-foreground)]">
                        {new Date(u.createdAt).toLocaleDateString()}
                      </span>
                    </div>
                  ))}
                </div>
              )}
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Newest Recipes</CardTitle>
            </CardHeader>
            <CardContent>
              {data.newestRecipes.length === 0 ? (
                <p className="text-sm text-[var(--muted-foreground)]">No recipes yet.</p>
              ) : (
                <div className="space-y-3">
                  {data.newestRecipes.map((r, i) => (
                    <div key={i} className="flex items-center justify-between text-sm">
                      <span className="font-medium">{r.title}</span>
                      <span className="text-[var(--muted-foreground)]">
                        {new Date(r.createdAt).toLocaleDateString()}
                      </span>
                    </div>
                  ))}
                </div>
              )}
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  )
}
