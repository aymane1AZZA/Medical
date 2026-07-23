import { useState } from 'react'
import {
  Activity,
  AlertOctagon,
  CircleStop,
  Clock3,
  Droplets,
  Gauge,
  HeartPulse,
  Pause,
  Play,
  ShieldCheck,
  Thermometer,
  TriangleAlert,
} from 'lucide-react'
import { useParams, useSearchParams } from 'react-router-dom'
import { toast } from 'sonner'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogMedia,
  AlertDialogTitle,
  AlertDialogTrigger,
} from '@/components/ui/alert-dialog'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Checkbox } from '@/components/ui/checkbox'
import { Progress } from '@/components/ui/progress'
import { Separator } from '@/components/ui/separator'
import { EmptyState, ErrorState, LoadingState } from '@/components/clinical/ClinicalStates'
import { PageHeader } from '@/components/clinical/PageHeader'
import { PatientContextHeader } from '@/components/clinical/PatientContextHeader'
import { StatusBadge } from '@/components/clinical/StatusBadge'
import { clinicalPatients } from '@/data/clinicalMocks'
import { useClinicalResource } from '@/hooks/useClinicalResource'
import { getSession } from '@/services/clinicalService'

function LiveValue({ label, value, unit, icon: Icon, detail }) {
  return <div className="border-r border-b bg-card p-3 text-card-foreground last:border-r-0"><div className="flex items-center justify-between gap-2"><p className="text-xs font-medium text-muted-foreground">{label}</p><Icon className="size-4 text-info" /></div><p className="mt-2 tabular-clinical text-2xl font-semibold leading-none">{value}<span className="ml-1 text-xs font-medium text-muted-foreground">{unit}</span></p>{detail && <p className="mt-2 text-xs text-muted-foreground">{detail}</p>}</div>
}

function CriticalAction({ type, onConfirm }) {
  const stop = type === 'stop'
  return <AlertDialog><AlertDialogTrigger asChild><Button variant={stop ? 'destructive' : 'outline'}>{stop ? <CircleStop data-icon="inline-start" /> : <AlertOctagon data-icon="inline-start" />}{stop ? 'Arrêt d’urgence' : 'Déclarer un incident'}</Button></AlertDialogTrigger><AlertDialogContent><AlertDialogHeader><AlertDialogMedia>{stop ? <CircleStop /> : <AlertOctagon />}</AlertDialogMedia><AlertDialogTitle>{stop ? 'Confirmer l’arrêt de la séance' : 'Ouvrir une déclaration d’incident'}</AlertDialogTitle><AlertDialogDescription>{stop ? 'Cette action arrête la pompe et nécessite un motif clinique ainsi qu’une traçabilité immédiate.' : 'Un incident horodaté sera ajouté au dossier et transmis au médecin responsable.'}</AlertDialogDescription></AlertDialogHeader><AlertDialogFooter><AlertDialogCancel>Annuler</AlertDialogCancel><AlertDialogAction variant={stop ? 'destructive' : 'default'} onClick={onConfirm}>{stop ? 'Arrêter et tracer' : 'Créer l’incident'}</AlertDialogAction></AlertDialogFooter></AlertDialogContent></AlertDialog>
}

export default function SessionCockpitPage() {
  const { sessionId } = useParams()
  const [searchParams] = useSearchParams()
  const state = searchParams.get('state') || undefined
  const { data: session, loading, error } = useClinicalResource(() => getSession(sessionId, { state }), [sessionId, state])
  const [paused, setPaused] = useState(false)
  const [checklist, setChecklist] = useState({})
  const patient = clinicalPatients.find((item) => item.id === session?.patientId)

  if (loading) return <div className="p-4"><LoadingState rows={9} label="Chargement du cockpit" /></div>
  if (error) return <div className="p-4"><ErrorState message={error} /></div>
  if (!session || !patient) return <div className="p-4"><EmptyState title="Aucune séance active" description="La séance demandée est terminée ou n’existe pas." /></div>

  const checked = (item) => checklist[item.id] ?? item.checked
  return <div className="min-w-0 bg-slate-100"><PatientContextHeader patient={patient} /><PageHeader title="Cockpit de séance" description={`${session.id} · ${session.procedure} · ${session.room}`} breadcrumbs={[{ label: 'Séances' }, { label: session.id }]} actions={<><Button variant="outline" onClick={() => { setPaused((value) => !value); toast.info(paused ? 'Séance reprise.' : 'Séance mise en pause et événement tracé.') }}>{paused ? <Play data-icon="inline-start" /> : <Pause data-icon="inline-start" />}{paused ? 'Reprendre' : 'Mettre en pause'}</Button><CriticalAction type="incident" onConfirm={() => toast.warning('Incident créé et médecin notifié.')} /><CriticalAction type="stop" onConfirm={() => toast.error('Arrêt d’urgence tracé.')} /></>} />
    <div className="flex flex-col gap-4 p-3 sm:p-4">
      <section className="overflow-hidden rounded-lg border border-slate-700 bg-slate-950 text-white">
        <div className="flex flex-col gap-4 border-b border-slate-700 p-4 lg:flex-row lg:items-center lg:justify-between"><div><div className="flex flex-wrap items-center gap-2"><StatusBadge status={paused ? 'warning' : session.status} label={paused ? 'En pause' : 'Séance en cours'} /><span className="text-xs text-slate-400">Démarrée à 10:30</span></div><p className="mt-3 tabular-clinical text-3xl font-semibold">01:12:36 <span className="text-sm font-normal text-slate-400">/ 02:00:00</span></p></div><div className="min-w-64 flex-1 lg:max-w-xl"><div className="mb-2 flex items-center justify-between text-xs"><span>Progression de séance</span><strong className="tabular-clinical">{session.progress} %</strong></div><Progress value={session.progress} className="h-2 bg-slate-700" /><p className="mt-2 text-xs text-slate-400">Volume plasmatique : {session.parameters.plasmaVolume.toLocaleString('fr-FR')} / {session.parameters.targetPlasma.toLocaleString('fr-FR')} mL</p></div></div>
        <div className="grid grid-cols-2 md:grid-cols-4 xl:grid-cols-8">
          <LiveValue label="Fréquence cardiaque" value={session.vitals.heartRate} unit="bpm" icon={HeartPulse} />
          <LiveValue label="Pression artérielle" value={`${session.vitals.systolic}/${session.vitals.diastolic}`} unit="mmHg" icon={Activity} />
          <LiveValue label="SpO₂" value={session.vitals.spo2} unit="%" icon={ShieldCheck} />
          <LiveValue label="Température" value={session.vitals.temperature} unit="°C" icon={Thermometer} />
          <LiveValue label="Débit sang" value={session.parameters.bloodFlow} unit="mL/min" icon={Gauge} />
          <LiveValue label="Volume traité" value={session.parameters.processedVolume.toLocaleString('fr-FR')} unit="mL" icon={Droplets} />
          <LiveValue label="Anticoagulant" value={session.parameters.anticoagulantRate} unit="mL/min" icon={Droplets} />
          <LiveValue label="Temps restant" value="00:47" unit="h:min" icon={Clock3} />
        </div>
      </section>

      {session.alerts.filter((alert) => alert.severity === 'warning').map((alert) => <Alert key={alert.id} className="border-warning bg-warning/10"><TriangleAlert /><AlertTitle>{alert.time} · {alert.title}</AlertTitle><AlertDescription><span>{alert.detail}</span><strong className="mt-1 block">Action attendue : {alert.action}</strong></AlertDescription></Alert>)}

      <div className="grid gap-4 xl:grid-cols-[1fr_0.8fr_0.8fr]">
        <Card><CardHeader><CardTitle>Checklist de séance</CardTitle><CardDescription>Chaque contrôle est horodaté et associé au PPR de l’opérateur.</CardDescription></CardHeader><CardContent><div className="flex flex-col gap-3">{session.checklist.map((item) => <label key={item.id} className="flex cursor-pointer items-start gap-3 rounded-lg border p-3"><Checkbox checked={checked(item)} onCheckedChange={(value) => setChecklist((current) => ({ ...current, [item.id]: Boolean(value) }))} /><span className="text-sm leading-5">{item.label}</span></label>)}</div></CardContent></Card>
        <Card><CardHeader><CardTitle>Équipement</CardTitle><CardDescription>État et traçabilité biomédicale.</CardDescription></CardHeader><CardContent><div className="flex items-center justify-between gap-3"><div><p className="font-semibold">{session.machine.name}</p><p className="text-xs text-muted-foreground">{session.machine.assetId}</p></div><StatusBadge status="success" label={session.machine.state} /></div><Separator className="my-4" /><dl className="grid gap-3 text-sm"><div className="flex justify-between gap-4"><dt className="text-muted-foreground">Maintenance prévue</dt><dd className="font-medium">12 août 2026</dd></div><div className="flex justify-between gap-4"><dt className="text-muted-foreground">Kit / lot</dt><dd className="font-mono text-xs">OPT-7A · L26071844</dd></div><div className="flex justify-between gap-4"><dt className="text-muted-foreground">Opérateur</dt><dd className="text-right font-medium">{session.operator}<br /><span className="text-xs text-muted-foreground">{session.operatorPpr}</span></dd></div></dl></CardContent></Card>
        <Card><CardHeader><CardTitle>Événements horodatés</CardTitle><CardDescription>Dernières actions de séance.</CardDescription></CardHeader><CardContent><ol className="flex flex-col gap-3">{session.events.map((event) => <li key={`${event.time}-${event.label}`} className="grid grid-cols-[42px_1fr] gap-3"><time className="tabular-clinical text-xs font-semibold text-primary">{event.time}</time><div><p className="text-sm font-medium">{event.label}</p><p className="mt-1 text-xs text-muted-foreground">{event.author}</p></div></li>)}</ol></CardContent></Card>
      </div>

      <Card><CardHeader><CardTitle>Clôture et compte rendu</CardTitle><CardDescription>Disponible lorsque la cible thérapeutique et la checklist sont complètes.</CardDescription></CardHeader><CardContent className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between"><p className="text-sm text-muted-foreground">Le compte rendu reprendra les paramètres, alertes, interventions et consommables tracés.</p><AlertDialog><AlertDialogTrigger asChild><Button disabled={!session.checklist.every(checked)}><ClipboardCheckIcon />Clôturer la séance</Button></AlertDialogTrigger><AlertDialogContent><AlertDialogHeader><AlertDialogTitle>Clôturer et signer le compte rendu ?</AlertDialogTitle><AlertDialogDescription>La séance passera en lecture seule. Une correction ultérieure nécessitera un addendum audité.</AlertDialogDescription></AlertDialogHeader><AlertDialogFooter><AlertDialogCancel>Continuer la séance</AlertDialogCancel><AlertDialogAction onClick={() => toast.success('Séance clôturée et compte rendu signé.')}>Clôturer et signer</AlertDialogAction></AlertDialogFooter></AlertDialogContent></AlertDialog></CardContent></Card>
    </div>
  </div>
}

function ClipboardCheckIcon() {
  return <ShieldCheck data-icon="inline-start" />
}
