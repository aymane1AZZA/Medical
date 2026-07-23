import { useEffect, useState } from 'react'
import { FileHeart, Search, UserRound } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import {
  CommandDialog,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from '@/components/ui/command'
import { clinicalPatients } from '@/data/clinicalMocks'

export function GlobalSearch() {
  const [open, setOpen] = useState(false)
  const navigate = useNavigate()

  useEffect(() => {
    const handler = (event) => {
      if (event.key.toLowerCase() === 'k' && (event.ctrlKey || event.metaKey)) {
        event.preventDefault()
        setOpen((current) => !current)
      }
    }
    window.addEventListener('keydown', handler)
    return () => window.removeEventListener('keydown', handler)
  }, [])

  const openPatient = (patientId) => {
    setOpen(false)
    navigate(`/medecin/patients/${patientId}`)
  }

  return (
    <>
      <Button
        variant="outline"
        size="icon"
        className="shrink-0 text-muted-foreground sm:h-8 sm:w-64 sm:justify-start sm:px-2.5"
        onClick={() => setOpen(true)}
        aria-label="Ouvrir la recherche clinique"
      >
        <Search data-icon="inline-start" />
        <span className="hidden truncate sm:inline">Rechercher IPP, patient...</span>
        <kbd className="ml-auto hidden rounded border bg-muted px-1.5 py-0.5 text-[10px] sm:inline">Ctrl K</kbd>
      </Button>
      <CommandDialog
        open={open}
        onOpenChange={setOpen}
        title="Recherche clinique"
        description="Rechercher un patient par IPP, nom ou CIN"
      >
        <CommandInput placeholder="IPP, nom, CIN..." />
        <CommandList>
          <CommandEmpty>Aucun patient trouvé.</CommandEmpty>
          <CommandGroup heading="Patients">
            {clinicalPatients.map((patient) => (
              <CommandItem
                key={patient.id}
                value={`${patient.ipp} ${patient.firstName} ${patient.lastName} ${patient.cin}`}
                onSelect={() => openPatient(patient.id)}
              >
                <UserRound />
                <div>
                  <p>{patient.firstName} {patient.lastName}</p>
                  <p className="text-xs text-muted-foreground">{patient.ipp} · {patient.cin}</p>
                </div>
                <FileHeart className="ml-auto" />
              </CommandItem>
            ))}
          </CommandGroup>
        </CommandList>
      </CommandDialog>
    </>
  )
}
