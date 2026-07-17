import { Outlet, Link, useLocation } from 'react-router-dom'
import {
  LayoutDashboard,
  UtensilsCrossed,
  FolderTree,
  Users,
  MessageSquare,
  ArrowLeft,
} from 'lucide-react'
import { cn } from '@/lib/utils'

const sidebarLinks = [
  { to: '/admin', icon: LayoutDashboard, label: 'Dashboard', exact: true },
  { to: '/admin/recipes', icon: UtensilsCrossed, label: 'Recipes' },
  { to: '/admin/categories', icon: FolderTree, label: 'Categories' },
  { to: '/admin/users', icon: Users, label: 'Users' },
  { to: '/admin/comments', icon: MessageSquare, label: 'Comments' },
]

export function AdminLayout() {
  const location = useLocation()

  return (
    <div className="flex min-h-screen">
      <aside className="hidden w-64 border-r border-[var(--border)] bg-[var(--background)] md:flex md:flex-col">
        <div className="flex h-16 items-center border-b border-[var(--border)] px-4">
          <Link to="/admin" className="flex items-center gap-2 font-semibold">
            <LayoutDashboard className="h-5 w-5 text-[var(--primary)]" />
            Admin Panel
          </Link>
        </div>
        <nav className="flex-1 space-y-1 p-4">
          {sidebarLinks.map((link) => {
            const isActive = link.exact
              ? location.pathname === link.to
              : location.pathname.startsWith(link.to)
            return (
              <Link
                key={link.to}
                to={link.to}
                className={cn(
                  'flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors',
                  isActive
                    ? 'bg-[var(--primary)]/10 text-[var(--primary)]'
                    : 'hover:bg-[var(--accent)]',
                )}
              >
                <link.icon size={18} />
                {link.label}
              </Link>
            )
          })}
        </nav>
        <div className="border-t border-[var(--border)] p-4">
          <Link
            to="/"
            className="flex items-center gap-2 text-sm text-[var(--muted-foreground)] hover:text-[var(--foreground)] transition-colors"
          >
            <ArrowLeft size={16} />
            Back to Site
          </Link>
        </div>
      </aside>
      <div className="flex-1">
        <Outlet />
      </div>
    </div>
  )
}
