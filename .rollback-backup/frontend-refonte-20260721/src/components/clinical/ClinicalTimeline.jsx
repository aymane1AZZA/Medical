import { Circle } from 'lucide-react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { StatusBadge } from './StatusBadge'

export function ClinicalTimeline({ items, title = 'Parcours clinique' }) {
  return (
    <Card>
      <CardHeader><CardTitle>{title}</CardTitle><CardDescription>Événements cliniques horodatés et traçables.</CardDescription></CardHeader>
      <CardContent>
        <ol className="flex flex-col">
          {items.map((item, index) => (
            <li key={item.id} className="relative grid grid-cols-[20px_1fr] gap-3 pb-5 last:pb-0">
              {index < items.length - 1 && <span className="absolute left-[9px] top-5 h-[calc(100%-8px)] w-px bg-border" />}
              <Circle className="mt-1 size-5 fill-background text-primary" aria-hidden="true" />
              <div className="min-w-0"><div className="flex flex-wrap items-center gap-2"><p className="font-semibold">{item.title}</p><StatusBadge status={item.tone} /></div><p className="mt-1 text-sm text-muted-foreground">{item.description}</p><time className="mt-2 block text-xs font-medium text-muted-foreground">{item.date}</time></div>
            </li>
          ))}
        </ol>
      </CardContent>
    </Card>
  )
}
