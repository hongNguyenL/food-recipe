import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { collectionsApi } from '@/api/collections'
import { CollectionGrid } from '@/components/ui/collection-grid'
import { CollectionSearchBar } from '@/components/ui/collection-search-bar'
import { Pagination } from '@/components/ui/pagination'
import { Select } from '@/components/ui/select'
import { LoadingSpinner } from '@/components/ui/loading-spinner'

export default function PublicCollectionsPage() {
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const [sort, setSort] = useState('createdAt,desc')

  const { data, isLoading, isError, refetch } = useQuery({
    queryKey: ['public-collections', page, sort],
    queryFn: () => collectionsApi.getPublicCollections({ page, size: 20, sort }),
  })

  const pageData = data?.data
  const collections = pageData?.content || []

  const filtered = search.trim()
    ? collections.filter((c) => c.name.toLowerCase().includes(search.toLowerCase()))
    : collections

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Public Collections</h1>
        <p className="text-[var(--muted-foreground)]">Discover collections created by the community</p>
      </div>

      <div className="flex flex-wrap gap-4 items-center">
        <CollectionSearchBar
          value={search}
          onChange={setSearch}
          onClear={() => setSearch('')}
        />
        <div className="w-40">
          <Select
            label="Sort"
            value={sort}
            onChange={(e) => { setSort(e.target.value); setPage(0) }}
            options={[
              { value: 'createdAt,desc', label: 'Newest' },
              { value: 'createdAt,asc', label: 'Oldest' },
              { value: 'name,asc', label: 'Name A-Z' },
              { value: 'name,desc', label: 'Name Z-A' },
            ]}
          />
        </div>
      </div>

      <CollectionGrid
        collections={filtered}
        isLoading={isLoading}
        isError={isError}
        onRetry={() => refetch()}
        showOwner
        emptyMessage="No public collections found"
      />

      {pageData && pageData.totalPages > 1 && (
        <Pagination page={pageData.number} totalPages={pageData.totalPages} onPageChange={setPage} />
      )}
    </div>
  )
}
