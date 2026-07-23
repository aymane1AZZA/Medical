import { useState } from 'react'
import { CalendarCheck, Clock3, MapPin, MonitorCog } from 'lucide-react'
import { useParams } from 'react-router-dom'
import { fr } from 'date-fns/locale'
import { toast } from 'sonner'
import { Button } from '@/components/ui/button'
import { Calendar } from '@/components/ui/calendar'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Field, FieldGroup, FieldLabel } from '@/components/ui/field'
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { PageHeader } from '@/components/clinical/PageHeader'
import { PatientContextHeader } from '@/components/clinical/PatientContextHeader'
import { LoadingState } from '@/components/clinical/ClinicalStates'
import { useClinicalResource } from '@/hooks/useClinicalResource'
import { getPatientRecord } from '@/services/clinicalService'

export default function PlanningPage() {
  const { patientId } = useParams()
  const [date, setDate] = useState(new Date(2026, 6, 22))
  const [slot, setSlot] = useState('09:30')
  const { data: record, loading } = useClinicalResource(() => getPatientRecord(patientId), [patientId])
  if (loading || !record) return <div className="p-6"><LoadingState rows={6} /></div>
  const { patient } = record
  return <div><PatientContextHeader patient={patient} /><PageHeader title="Planifier une séance" description="Coordonner le patient, l’équipe, la salle et l’équipement disponible." breadcrumbs={[{ label: 'Patients', to: '/medecin/patients' }, { label: `${patient.firstName} ${patient.lastName}`, to: `/medecin/patients/${patient.id}` }, { label: 'Planification' }]} />
    <div className="grid gap-4 p-4 sm:p-6 xl:grid-cols-[360px_1fr]"><Card><CardHeader><CardTitle>Date de séance</CardTitle><CardDescription>Sélectionnez une date compatible avec le protocole.</CardDescription></CardHeader><CardContent><Calendar mode="single" selected={date} onSelect={setDate} locale={fr} disabled={{ before: new Date(2026, 6, 20) }} /></CardContent></Card><div className="flex flex-col gap-4"><Card><CardHeader><CardTitle>Ressources</CardTitle><CardDescription>Les disponibilités sont vérifiées avant confirmation.</CardDescription></CardHeader><CardContent><FieldGroup><Field><FieldLabel>Créneau</FieldLabel><Select value={slot} onValueChange={setSlot}><SelectTrigger className="w-full"><SelectValue /></SelectTrigger><SelectContent><SelectGroup><SelectItem value="08:00">08:00 – 10:00</SelectItem><SelectItem value="09:30">09:30 – 11:30</SelectItem><SelectItem value="13:30">13:30 – 15:30</SelectItem></SelectGroup></SelectContent></Select></Field><Field><FieldLabel>Salle</FieldLabel><Select defaultValue="APH-02"><SelectTrigger className="w-full"><SelectValue /></SelectTrigger><SelectContent><SelectGroup><SelectItem value="APH-01">Salle APH-01</SelectItem><SelectItem value="APH-02">Salle APH-02</SelectItem></SelectGroup></SelectContent></Select></Field><Field><FieldLabel>Équipement</FieldLabel><Select defaultValue="OPTIA-4"><SelectTrigger className="w-full"><SelectValue /></SelectTrigger><SelectContent><SelectGroup><SelectItem value="OPTIA-4">Spectra Optia 4 · Disponible</SelectItem><SelectItem value="OPTIA-2">Spectra Optia 2 · Maintenance à 17:00</SelectItem></SelectGroup></SelectContent></Select></Field></FieldGroup></CardContent></Card><Card><CardHeader><CardTitle>Récapitulatif opérationnel</CardTitle></CardHeader><CardContent><dl className="grid gap-3 sm:grid-cols-2"><div className="flex gap-2"><CalendarCheck className="size-4 text-primary" /><div><dt className="text-xs text-muted-foreground">Date</dt><dd className="font-medium">{date?.toLocaleDateString('fr-FR', { weekday: 'long', day: '2-digit', month: 'long' })}</dd></div></div><div className="flex gap-2"><Clock3 className="size-4 text-primary" /><div><dt className="text-xs text-muted-foreground">Créneau</dt><dd className="font-medium">{slot} · durée estimée 2 h</dd></div></div><div className="flex gap-2"><MapPin className="size-4 text-primary" /><div><dt className="text-xs text-muted-foreground">Lieu</dt><dd className="font-medium">Unité d’aphérèse · APH-02</dd></div></div><div className="flex gap-2"><MonitorCog className="size-4 text-primary" /><div><dt className="text-xs text-muted-foreground">Machine</dt><dd className="font-medium">Spectra Optia 4 · BIO-APH-004</dd></div></div></dl><Button className="mt-5" onClick={() => toast.success('Séance planifiée et équipe notifiée.')}><CalendarCheck data-icon="inline-start" />Confirmer la planification</Button></CardContent></Card></div></div>
  </div>
}
