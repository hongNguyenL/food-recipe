import { lazy, Suspense } from 'react'
import { createBrowserRouter, Navigate } from 'react-router-dom'
import { MainLayout } from '@/layouts/main-layout'
import { AdminLayout } from '@/layouts/admin-layout'
import { ProtectedRoute } from '@/components/ui/protected-route'
import { LoadingSpinner } from '@/components/ui/loading-spinner'

const HomePage = lazy(() => import('@/pages/home'))
const LoginPage = lazy(() => import('@/pages/login'))
const RegisterPage = lazy(() => import('@/pages/register'))
const SearchPage = lazy(() => import('@/pages/search'))
const TopRatedPage = lazy(() => import('@/pages/top-rated'))
const PopularPage = lazy(() => import('@/pages/popular'))
const LatestPage = lazy(() => import('@/pages/latest'))
const RecipeDetailPage = lazy(() => import('@/pages/recipe-detail'))
const CategoriesPage = lazy(() => import('@/pages/categories'))
const CategoryRecipesPage = lazy(() => import('@/pages/category-recipes'))
const DashboardPage = lazy(() => import('@/pages/dashboard'))
const MyCollectionsPage = lazy(() => import('@/pages/my-collections'))
const PublicCollectionsPage = lazy(() => import('@/pages/public-collections'))
const CollectionDetailPage = lazy(() => import('@/pages/collection-detail'))
const CreateCollectionPage = lazy(() => import('@/pages/create-collection'))
const EditCollectionPage = lazy(() => import('@/pages/edit-collection'))
const NotFoundPage = lazy(() => import('@/pages/not-found'))

const AdminDashboardPage = lazy(() => import('@/pages/admin/dashboard'))
const AdminRecipesPage = lazy(() => import('@/pages/admin/recipes'))
const AdminCategoriesPage = lazy(() => import('@/pages/admin/categories'))
const AdminUsersPage = lazy(() => import('@/pages/admin/users'))
const AdminCommentsPage = lazy(() => import('@/pages/admin/comments'))

function SuspenseWrapper({ children }: { children: React.ReactNode }) {
  return <Suspense fallback={<LoadingSpinner />}>{children}</Suspense>
}

export const router = createBrowserRouter([
  {
    path: '/',
    element: <MainLayout />,
    children: [
      { index: true, element: <SuspenseWrapper><HomePage /></SuspenseWrapper> },
      { path: 'login', element: <SuspenseWrapper><LoginPage /></SuspenseWrapper> },
      { path: 'register', element: <SuspenseWrapper><RegisterPage /></SuspenseWrapper> },
      { path: 'recipes', element: <SuspenseWrapper><SearchPage /></SuspenseWrapper> },
      { path: 'recipes/:id', element: <SuspenseWrapper><RecipeDetailPage /></SuspenseWrapper> },
      { path: 'categories', element: <SuspenseWrapper><CategoriesPage /></SuspenseWrapper> },
      { path: 'categories/:id', element: <SuspenseWrapper><CategoryRecipesPage /></SuspenseWrapper> },
      { path: 'top-rated', element: <SuspenseWrapper><TopRatedPage /></SuspenseWrapper> },
      { path: 'popular', element: <SuspenseWrapper><PopularPage /></SuspenseWrapper> },
      { path: 'latest', element: <SuspenseWrapper><LatestPage /></SuspenseWrapper> },
      {
        path: 'dashboard',
        element: (
          <ProtectedRoute>
            <SuspenseWrapper><DashboardPage /></SuspenseWrapper>
          </ProtectedRoute>
        ),
      },
      { path: 'collections', element: <SuspenseWrapper><PublicCollectionsPage /></SuspenseWrapper> },
      { path: 'collections/:id', element: <SuspenseWrapper><CollectionDetailPage /></SuspenseWrapper> },
      {
        path: 'my-collections',
        element: (
          <ProtectedRoute>
            <SuspenseWrapper><MyCollectionsPage /></SuspenseWrapper>
          </ProtectedRoute>
        ),
      },
      {
        path: 'my-collections/new',
        element: (
          <ProtectedRoute>
            <SuspenseWrapper><CreateCollectionPage /></SuspenseWrapper>
          </ProtectedRoute>
        ),
      },
      {
        path: 'my-collections/:id/edit',
        element: (
          <ProtectedRoute>
            <SuspenseWrapper><EditCollectionPage /></SuspenseWrapper>
          </ProtectedRoute>
        ),
      },
      { path: '404', element: <SuspenseWrapper><NotFoundPage /></SuspenseWrapper> },
      { path: '*', element: <Navigate to="/404" replace /> },
    ],
  },
  {
    path: '/admin',
    element: (
      <ProtectedRoute requireAdmin>
        <AdminLayout />
      </ProtectedRoute>
    ),
    children: [
      { index: true, element: <SuspenseWrapper><AdminDashboardPage /></SuspenseWrapper> },
      { path: 'recipes', element: <SuspenseWrapper><AdminRecipesPage /></SuspenseWrapper> },
      { path: 'categories', element: <SuspenseWrapper><AdminCategoriesPage /></SuspenseWrapper> },
      { path: 'users', element: <SuspenseWrapper><AdminUsersPage /></SuspenseWrapper> },
      { path: 'comments', element: <SuspenseWrapper><AdminCommentsPage /></SuspenseWrapper> },
    ],
  },
])
