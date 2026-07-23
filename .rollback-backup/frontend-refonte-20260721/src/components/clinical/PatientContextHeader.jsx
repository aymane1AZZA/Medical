import { AlertTriangle, CalendarDays, Droplets, MapPin } from 'lucide-react'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Separator } from '@/components/ui/separator'
import { StatusBadge } from './StatusBadge'

export function PatientContextHeader({ patient }) {
  const initials = `${patient.firstName[0]}${patient.lastName[0]}`
  return (
    <section aria-label="Contexte patient" className="sticky top-14 border-b bg-card/95 px-4 py-3 backdrop-blur sm:px-6">
      <div className="flex flex-col gap-3 xl:flex-row xl:items-center xl:justify-between">
        <div className="flex min-w-0 items-center gap-3">
          <Avatar className="size-10"><AvatarFallback>{initials}</AvatarFallback></Avatar>
          <div className="min-w-0"><p className="truncate font-semibold">{patient.firstName} {patient.lastName}</p><p className="text-xs text-muted-foreground">IPP {patient.ipp} · CIN {patient.cin}</p></div>
          <StatusBadge status={patient.eligibility} />
        </div>
        <div className="flex flex-wrap items-center gap-3 text-xs text-muted-foreground sm:gap-4">
          <span className="inline-flex items-center gap-1.5"><CalendarDays className="size-4" />{patient.age} ans · {patient.sex}</span>
          <Separator orientation="vertical" className="hidden h-4 sm:block" />
          <span className="inline-flex items-center gap-1.5"><Droplets className="size-4" />{patient.bloodGroup}</span>
          <Separator orientation="vertical" className="hidden h-4 sm:block" />
          <span className="inline-flex items-center gap-1.5"><MapPin className="size-4" />{patient.city}</span>
          {patient.allergies.length > 0 && <span className="inline-flex items-center gap-1.5 font-semibold text-destructive"><AlertTriangle className="size-4" />Allergie : {patient.allergies.join(', ')}</span>}
        </div>
      </div>
    </section>
  )
}
