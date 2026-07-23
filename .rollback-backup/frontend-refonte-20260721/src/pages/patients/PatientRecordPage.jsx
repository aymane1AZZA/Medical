import { CalendarPlus, ClipboardCheck, FileText, FlaskConical, Phone, Stethoscope } from 'lucide-react'
import { Link, useParams, useSearchParams } from 'react-router-dom'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { AuditTrail } from '@/components/clinical/AuditTrail'
import { ClinicalTimeline } from '@/components/clinical/ClinicalTimeline'
import { EmptyState, ErrorState, LoadingState } from '@/components/clinical/ClinicalStates'
import { PageHeader } from '@/components/clinical/PageHeader'
import { PatientContextHeader } from '@/components/clinical/PatientContextHeader'
import { StatusBadge } from '@/components/clinical/StatusBadge'
import { useClinicalResource } from '@/hooks/useClinicalResource'
import { getPatientRecord } from '@/services/clinicalService'

function BiologyPanel({ results }) {
  return (
    <Card><CardHeader><CardTitle>Derniers bilans biologiques</CardTitle><CardDescription>Résultats validés, intervalles de référence et historique immédiat.</CardDescription></CardHeader><CardContent className="overflow-x-auto">
      <Table><TableHeader><TableRow><TableHead>Bilan</TableHead><TableHead>Analyse</TableHead><TableHead>Résultat</TableHead><TableHead>Référence</TableHead><TableHead>Statut</TableHead><TableHead>Date / laboratoire</TableHead><TableHead>Validation</TableHead></TableRow></TableHeader>
        <TableBody>{results.map((result) => <TableRow key={result.id}><TableCell>{result.category}</TableCell><TableCell className="font-medium">{result.analyte}</TableCell><TableCell><span className="tabular-clinical font-semibold">{result.value}</span> <span className="text-xs text-muted-foreground">{result.unit}</span></TableCell><TableCell className="tabular-clinical">{result.reference}</TableCell><TableCell><StatusBadge status={result.status} /></TableCell><TableCell><p className="whitespace-nowrap text-xs">{new Intl.DateTimeFormat('fr-FR', { dateStyle: 'short', timeStyle: 'short' }).format(new Date(result.date))}</p><p className="mt-1 min-w-44 text-xs text-muted-foreground">{result.laboratory}</p></TableCell><TableCell><p className="min-w-44 text-xs">{result.validatedBy}</p><p className="mt-1 text-xs text-muted-foreground">Historique : {result.history.join(' → ')}</p></TableCell></TableRow>)}</TableBody>
      </Table>
    </CardContent></Card>
  )
}

function PrescriptionPanel({ prescription }) {
  if (!prescription) return <EmptyState title="Aucune prescription active" />
  const items = [
    ['Procédure', prescription.procedure], ['Indication', prescription.indication], ['Volume cible', `${prescription.targetVolume.toLocaleString('fr-FR')} mL`],
    ['Liquide de substitution', prescription.replacementFluid], ['Anticoagulant', `${prescription.anticoagulant} · ratio ${prescription.ratio}`], ['Fréquence', prescription.frequency],
  ]
  return <Card><CardHeader><div className="flex flex-wrap items-start justify-between gap-3"><div><CardTitle>Prescription {prescription.id}</CardTitle><CardDescription>Signée par {prescription.prescriber} · {prescription.npe}</CardDescription></div><StatusBadge status={prescription.status} /></div></CardHeader><CardContent><dl className="grid gap-px overflow-hidden rounded-lg border bg-border sm:grid-cols-2 xl:grid-cols-3">{items.map(([label, value]) => <div key={label} className="bg-card p-4"><dt className="text-xs font-medium text-muted-foreground">{label}</dt><dd className="mt-1 text-sm font-semibold">{value}</dd></div>)}</dl><div className="mt-4 flex items-center justify-between gap-3 rounded-lg bg-muted p-3"><p className="text-sm"><strong>{prescription.sessionsCompleted}</strong> séances réalisées sur <strong>{prescription.sessionsPlanned}</strong></p><Button variant="outline"><FileText data-icon="inline-start" />Voir le document signé</Button></div></CardContent></Card>
}

function SummaryPanel({ record }) {
  const { patient, prescription, timeline } = record
  return <div className="grid gap-4 xl:grid-cols-[0.8fr_1.2fr]"><div className="flex flex-col gap-4"><Card><CardHeader><CardTitle>Synthèse médicale</CardTitle><CardDescription>Informations utiles à la décision d’aphérèse.</CardDescription></CardHeader><CardContent><dl className="grid gap-4 sm:grid-cols-2"><div><dt className="text-xs text-muted-foreground">Indication</dt><dd className="mt-1 font-medium">{patient.diagnosis}</dd></div><div><dt className="text-xs text-muted-foreground">Protocole</dt><dd className="mt-1 font-medium">{patient.protocol}</dd></div><div><dt className="text-xs text-muted-foreground">Médecin référent</dt><dd className="mt-1 font-medium">{patient.referringPhysician.name}</dd><dd className="text-xs text-muted-foreground">{patient.referringPhysician.npe}</dd></div><div><dt className="text-xs text-muted-foreground">Contact</dt><dd className="mt-1 inline-flex items-center gap-1.5 font-medium"><Phone className="size-4" />{patient.phone}</dd></div></dl></CardContent></Card><Alert><ClipboardCheck /><AlertTitle>Éligibilité confirmée</AlertTitle><AlertDescription>Prescription active et bilans compatibles avec la séance planifiée. La créatinine et la glycémie restent à surveiller.</AlertDescription></Alert>{prescription && <Card><CardHeader><CardTitle>Progression du protocole</CardTitle></CardHeader><CardContent><p className="text-3xl font-semibold tabular-clinical">{prescription.sessionsCompleted}/{prescription.sessionsPlanned}</p><p className="mt-1 text-sm text-muted-foreground">séances réalisées</p></CardContent></Card>}</div><ClinicalTimeline items={timeline} /></div>
}

export default function PatientRecordPage() {
  const { patientId } = useParams()
  const [searchParams, setSearchParams] = useSearchParams()
  const testState = searchParams.get('state') || undefined
  const activeTab = searchParams.get('tab') || 'summary'
  const { data: record, loading, error } = useClinicalResource(() => getPatientRecord(patientId, { state: testState }), [patientId, testState])

  if (loading) return <div className="p-4 sm:p-6"><LoadingState rows={8} /></div>
  if (error) return <div className="p-4 sm:p-6"><ErrorState message={error} /></div>
  if (!record) return <div className="p-4 sm:p-6"><EmptyState title="Dossier patient introuvable" description="Vérifiez l’IPP ou revenez à la liste des patients." action={<Button asChild><Link to="/medecin/patients">Retour aux patients</Link></Button>} /></div>

  const { patient } = record
  return <div className="min-w-0"><PatientContextHeader patient={patient} /><PageHeader title="Dossier patient" description={`${patient.diagnosis} · ${patient.protocol}`} breadcrumbs={[{ label: 'Patients', to: '/medecin/patients' }, { label: `${patient.firstName} ${patient.lastName}` }]} actions={<><Button variant="outline"><Stethoscope data-icon="inline-start" />Réévaluer l’éligibilité</Button><Button asChild><Link to={`/medecin/patients/${patient.id}/planification`}><CalendarPlus data-icon="inline-start" />Planifier</Link></Button></>} />
    <div className="p-4 sm:p-6"><Tabs value={activeTab} onValueChange={(value) => setSearchParams(value === 'summary' ? {} : { tab: value })}><TabsList className="mb-4 h-auto w-full justify-start overflow-x-auto"><TabsTrigger value="summary">Synthèse</TabsTrigger><TabsTrigger value="biology"><FlaskConical />Bilans biologiques</TabsTrigger><TabsTrigger value="prescription"><FileText />Prescription</TabsTrigger><TabsTrigger value="timeline">Parcours et audit</TabsTrigger></TabsList><TabsContent value="summary"><SummaryPanel record={record} /></TabsContent><TabsContent value="biology"><BiologyPanel results={record.biology} /></TabsContent><TabsContent value="prescription"><PrescriptionPanel prescription={record.prescription} /></TabsContent><TabsContent value="timeline"><div className="grid gap-4 xl:grid-cols-2"><ClinicalTimeline items={record.timeline} /><AuditTrail entries={record.audit} /></div></TabsContent></Tabs></div>
  </div>
}
