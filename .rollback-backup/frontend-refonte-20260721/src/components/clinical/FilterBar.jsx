import { Search, X } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'

export function FilterBar({ query, onQueryChange, status, onStatusChange, resultCount }) {
  return (
    <section aria-label="Filtres patients" className="clinical-panel flex flex-col gap-3 p-3 lg:flex-row lg:items-center">
      <div className="relative min-w-0 flex-1">
        <Search className="pointer-events-none absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
        <Input className="h-10 pl-9" value={query} onChange={(event) => onQueryChange(event.target.value)} placeholder="Rechercher par IPP, nom ou CIN" aria-label="Rechercher un patient" />
      </div>
      <Select value={status} onValueChange={onStatusChange}>
        <SelectTrigger className="h-10 w-full lg:w-48" aria-label="Filtrer par éligibilité"><SelectValue placeholder="Tous les statuts" /></SelectTrigger>
        <SelectContent><SelectGroup>
          <SelectItem value="all">Tous les statuts</SelectItem>
          <SelectItem value="eligible">Éligible</SelectItem>
          <SelectItem value="pending">À valider</SelectItem>
          <SelectItem value="ineligible">Non éligible</SelectItem>
        </SelectGroup></SelectContent>
      </Select>
      {(query || status !== 'all') && <Button variant="ghost" onClick={() => { onQueryChange(''); onStatusChange('all') }}><X data-icon="inline-start" />Réinitialiser</Button>}
      <p className="shrink-0 text-sm text-muted-foreground" aria-live="polite">{resultCount} patient{resultCount > 1 ? 's' : ''}</p>
    </section>
  )
}
