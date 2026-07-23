import { FlaskConical, HeartPulse, Microscope, ShieldCheck, Stethoscope, UserRound } from 'lucide-react'
import { FieldError, FieldLegend, FieldSet } from '@/components/ui/field'
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group'
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { cn } from '@/lib/utils'
import { roles } from '@/utils/roles'

const icons = {
  ROLE_ADMIN: ShieldCheck,
  ROLE_MEDECIN: Stethoscope,
  ROLE_INFERMIER: HeartPulse,
  ROLE_BIOMEDICAL: Microscope,
  ROLE_PATIENT: UserRound,
  ROLE_LABO: FlaskConical,
}

export default function RoleSelector({ value, onChange, error }) {
  return (
    <FieldSet data-invalid={Boolean(error)}>
      <FieldLegend variant="label">Profil d’accès</FieldLegend>
      <div className="sm:hidden">
        <Select value={value} onValueChange={onChange}><SelectTrigger className="h-10 w-full" aria-invalid={Boolean(error)}><SelectValue placeholder="Sélectionner votre profil" /></SelectTrigger><SelectContent><SelectGroup>{roles.map((role) => <SelectItem key={role.value} value={role.value}>{role.label}</SelectItem>)}</SelectGroup></SelectContent></Select>
      </div>
      <RadioGroup value={value} onValueChange={onChange} className="hidden grid-cols-2 gap-2 sm:grid">
        {roles.map((role) => {
          const Icon = icons[role.value]
          const selected = value === role.value
          return <div key={role.value} className="relative"><RadioGroupItem id={role.value} value={role.value} className="peer absolute size-px opacity-0" /><label htmlFor={role.value} className={cn('flex min-h-20 cursor-pointer items-start gap-3 rounded-lg border bg-card p-3 transition-colors duration-150 hover:bg-muted peer-focus-visible:ring-[3px] peer-focus-visible:ring-ring/30', selected && 'border-primary bg-accent')}><span className="grid size-9 shrink-0 place-items-center rounded-lg bg-secondary text-primary"><Icon className="size-4" aria-hidden="true" /></span><span className="min-w-0"><span className="block text-sm font-semibold">{role.label}</span><span className="mt-1 block text-xs text-muted-foreground">{role.description}</span></span></label></div>
        })}
      </RadioGroup>
      {error && <FieldError>{error.message}</FieldError>}
    </FieldSet>
  )
}
