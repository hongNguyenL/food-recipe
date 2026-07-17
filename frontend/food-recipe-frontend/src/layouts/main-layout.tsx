import { Outlet } from 'react-router-dom'
import { Navbar } from '@/components/ui/navbar'
import { Footer } from '@/components/ui/footer'

export function MainLayout() {
  return (
    <div className="flex min-h-screen flex-col">
      <Navbar />
      <main className="flex-1">
        <Outlet />
      </main>
      <Footer />
    </div>
  )
}
