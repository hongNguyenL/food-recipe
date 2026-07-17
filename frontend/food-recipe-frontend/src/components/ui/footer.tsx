export function Footer() {
  return (
    <footer className="border-t border-[var(--border)] bg-[var(--background)]">
      <div className="mx-auto max-w-7xl px-4 py-8">
        <div className="grid grid-cols-1 gap-8 md:grid-cols-3">
          <div>
            <h3 className="font-semibold mb-3">RecipeBox</h3>
            <p className="text-sm text-[var(--muted-foreground)]">
              Discover and share amazing recipes from around the world.
            </p>
          </div>
          <div>
            <h3 className="font-semibold mb-3">Quick Links</h3>
            <ul className="space-y-2 text-sm text-[var(--muted-foreground)]">
              <li><a href="/recipes" className="hover:text-[var(--foreground)] transition-colors">All Recipes</a></li>
              <li><a href="/categories" className="hover:text-[var(--foreground)] transition-colors">Categories</a></li>
              <li><a href="/popular" className="hover:text-[var(--foreground)] transition-colors">Popular</a></li>
              <li><a href="/top-rated" className="hover:text-[var(--foreground)] transition-colors">Top Rated</a></li>
            </ul>
          </div>
          <div>
            <h3 className="font-semibold mb-3">About</h3>
            <p className="text-sm text-[var(--muted-foreground)]">
              A community-driven recipe platform built with React and Spring Boot.
            </p>
          </div>
        </div>
        <div className="mt-8 border-t border-[var(--border)] pt-4 text-center text-sm text-[var(--muted-foreground)]">
          &copy; {new Date().getFullYear()} RecipeBox. All rights reserved.
        </div>
      </div>
    </footer>
  )
}
