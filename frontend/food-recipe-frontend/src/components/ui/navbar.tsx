import { Link, useNavigate } from 'react-router-dom'
import { UtensilsCrossed, Moon, Sun, LogOut, User, Settings, LayoutDashboard, Refrigerator } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { useAuth } from '@/hooks/use-auth'
import { useTheme } from '@/hooks/use-theme'
import { useState } from 'react'

export function Navbar() {
  const { isAuthenticated, isAdmin, user, logout } = useAuth()
  const { isDark, toggle } = useTheme()
  const navigate = useNavigate()
  const [menuOpen, setMenuOpen] = useState(false)

  const handleLogout = () => {
    logout()
    navigate('/')
  }

  return (
    <nav className="sticky top-0 z-40 border-b border-[var(--border)] bg-[var(--background)]/95 backdrop-blur supports-[backdrop-filter]:bg-[var(--background)]/80">
      <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-4">
        <Link to="/" className="flex items-center gap-2 text-xl font-bold">
          <UtensilsCrossed className="h-6 w-6 text-[var(--primary)]" />
          RecipeBox
        </Link>

        <div className="hidden md:flex items-center gap-6">
          <Link to="/recipes" className="text-sm font-medium hover:text-[var(--primary)] transition-colors">Recipes</Link>
          <Link to="/pantry-search" className="flex items-center gap-1 text-sm font-medium hover:text-[var(--primary)] transition-colors">
            <Refrigerator size={16} />
            Pantry
          </Link>
          <Link to="/categories" className="text-sm font-medium hover:text-[var(--primary)] transition-colors">Categories</Link>
          <Link to="/collections" className="text-sm font-medium hover:text-[var(--primary)] transition-colors">Collections</Link>
          <Link to="/top-rated" className="text-sm font-medium hover:text-[var(--primary)] transition-colors">Top Rated</Link>
          <Link to="/popular" className="text-sm font-medium hover:text-[var(--primary)] transition-colors">Popular</Link>
          <Link to="/latest" className="text-sm font-medium hover:text-[var(--primary)] transition-colors">Latest</Link>
        </div>

        <div className="flex items-center gap-2">
          <button onClick={toggle} className="rounded-md p-2 hover:bg-[var(--accent)] transition-colors">
            {isDark ? <Sun size={18} /> : <Moon size={18} />}
          </button>

          {isAuthenticated ? (
            <div className="relative">
              <button
                onClick={() => setMenuOpen(!menuOpen)}
                className="flex items-center gap-2 rounded-md p-2 hover:bg-[var(--accent)] transition-colors"
              >
                <User size={18} />
                <span className="text-sm hidden sm:inline">{user?.username}</span>
              </button>
              {menuOpen && (
                <>
                  <div className="fixed inset-0 z-40" onClick={() => setMenuOpen(false)} />
                  <div className="absolute right-0 top-full z-50 mt-1 w-48 rounded-md border border-[var(--border)] bg-[var(--background)] shadow-lg">
                    <Link
                      to="/dashboard"
                      className="flex items-center gap-2 px-4 py-2 text-sm hover:bg-[var(--accent)] transition-colors"
                      onClick={() => setMenuOpen(false)}
                    >
                      <User size={16} />
                      Dashboard
                    </Link>
                    <Link
                      to="/my-collections"
                      className="flex items-center gap-2 px-4 py-2 text-sm hover:bg-[var(--accent)] transition-colors"
                      onClick={() => setMenuOpen(false)}
                    >
                      Collections
                    </Link>
                    {isAdmin && (
                      <Link
                        to="/admin"
                        className="flex items-center gap-2 px-4 py-2 text-sm hover:bg-[var(--accent)] transition-colors"
                        onClick={() => setMenuOpen(false)}
                      >
                        <LayoutDashboard size={16} />
                        Admin
                      </Link>
                    )}
                    <button
                      onClick={() => { handleLogout(); setMenuOpen(false) }}
                      className="flex w-full items-center gap-2 px-4 py-2 text-sm hover:bg-[var(--accent)] transition-colors"
                    >
                      <LogOut size={16} />
                      Logout
                    </button>
                  </div>
                </>
              )}
            </div>
          ) : (
            <div className="flex items-center gap-2">
              <Button variant="ghost" size="sm" onClick={() => navigate('/login')}>Login</Button>
              <Button size="sm" onClick={() => navigate('/register')}>Register</Button>
            </div>
          )}
        </div>
      </div>
    </nav>
  )
}
