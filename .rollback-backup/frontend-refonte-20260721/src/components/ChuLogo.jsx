import { Cross } from 'lucide-react'
import { cn } from '@/lib/utils'

export default function ChuLogo({ compact = false, inverse = false }) {
  return (
    <div className="flex items-center gap-2.5">
      <span className={cn('grid size-9 shrink-0 place-items-center rounded-lg border', inverse ? 'border-white/20 bg-white text-primary' : 'border-border bg-card text-primary')}>
        <Cross className="size-5" strokeWidth={2.5} aria-hidden="true" />
      </span>
      {!compact && <div className="min-w-0"><p className={cn('text-[10px] font-semibold uppercase tracking-[0.16em]', inverse ? 'text-sidebar-foreground/70' : 'text-muted-foreground')}>Centre hospitalier</p><p className={cn('truncate text-sm font-semibold', inverse ? 'text-sidebar-foreground' : 'text-foreground')}>Aphérèse clinique</p></div>}
    </div>
  )
}
