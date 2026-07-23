import { useMemo, useState } from 'react'
import { CalendarCheck, Clock3, ShieldAlert, Users } from 'lucide-react'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import { Pagination, PaginationContent, PaginationItem, PaginationLink, PaginationNext, PaginationPrevious } from '@/components/ui/pagination'
import { DataTable } from '@/components/clinical/DataTable'
import { ErrorState, LoadingState } from '@/components/clinical/ClinicalStates'
import { FilterBar } from '@/components/clinical/FilterBar'
import { MetricCard } from '@/components/clinical/MetricCard'
import { PageHeader } from '@/components/clinical/PageHeader'
import { StatusBadge } from '@/components/clinical/StatusBadge'
import { useClinicalResource } from '@/hooks/useClinicalResource'
import { listPatients } from '@/services/clinicalService'

const formatSession = (value) => value ? new Intl.DateTimeFormat('fr-FR', { day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit' }).format(new Date(value)) : 'Non planifiée'

export default function PatientsListPage() {
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const testState = searchParams.get('state') || undefined
  const [query, setQuery] = useState('')
  const [status, setStatus] = useState('all')
  const { data: patients, loading, error } = useClinicalResource(() => listPatients({ state: testState }), [testState])

  const filteredPatients = useMemo(() => {
    if (!patients) return []
    const normalized = query.trim().toLocaleLowerCase('fr')
    return patients.filter((patient) => {
      const matchesQuery = !normalized || `${patient.ipp} ${patient.cin} ${patient.firstName} ${patient.lastName}`.toLocaleLowerCase('fr').includes(normalized)
      return matchesQuery && (status === 'all' || patient.eligibility === status)
    })
  }, [patients, query, status])

  const columns = [
    { key: 'patient', label: 'Patient', render: (patient) => <div><Link className="font-semibold text-foreground hover:text-primary hover:underline" to={`/medecin/patients/${patient.id}`}>{patient.firstName} {patient.lastName}</Link><p className="mt-1 text-xs text-muted-foreground">{patient.cin} · {patient.age} ans</p></div> },
    { key: 'ipp', label: 'IPP', render: (patient) => <span className="font-mono text-xs">{patient.ipp}</span> },
    { key: 'diagnosis', label: 'Indication', render: (patient) => <span className="block min-w-52 max-w-72 text-sm">{patient.diagnosis}</span> },
    { key: 'protocol', label: 'Protocole', render: (patient) => <span className="block min-w-48 text-sm">{patient.protocol}</span> },
    { key: 'eligibility', label: 'Éligibilité', render: (patient) => <StatusBadge status={patient.eligibility} /> },
    { key: 'nextSession', label: 'Prochaine séance', render: (patient) => <span className="whitespace-nowrap text-sm tabular-clinical">{formatSession(patient.nextSession)}</span> },
  ]

  return (
    <div className="min-w-0">
      <PageHeader title="Patients d’aphérèse" description="Rechercher, qualifier et suivre les patients engagés dans un parcours d’aphérèse." breadcrumbs={[{ label: 'Patients' }]} actions={<Button asChild><Link to="/medecin/patients/pat-001"><Users data-icon="inline-start" />Ouvrir le dossier prioritaire</Link></Button>} />
      <div className="flex flex-col gap-4 p-4 sm:p-6">
        <section aria-label="Indicateurs opérationnels" className="grid grid-cols-2 gap-3 xl:grid-cols-4">
          <MetricCard label="Patients actifs" value="38" detail="5 protocoles en cours" icon={Users} />
          <MetricCard label="Séances aujourd’hui" value="8" detail="3 en cours, 5 planifiées" icon={CalendarCheck} />
          <MetricCard label="Éligibilités à valider" value="4" detail="2 avant 12:00" icon={Clock3} />
          <MetricCard label="Bilans à contrôler" value="2" detail="1 résultat critique" icon={ShieldAlert} />
        </section>
        {loading && <LoadingState rows={6} />}
        {error && <ErrorState message={error} />}
        {patients && <>
          <FilterBar query={query} onQueryChange={setQuery} status={status} onStatusChange={setStatus} resultCount={filteredPatients.length} />
          <DataTable columns={columns} rows={filteredPatients} onRowOpen={(patient) => navigate(`/medecin/patients/${patient.id}`)} emptyTitle="Aucun patient trouvé" />
          {filteredPatients.length > 0 && <Pagination><PaginationContent><PaginationItem><PaginationPrevious href="#" /></PaginationItem><PaginationItem><PaginationLink href="#" isActive>1</PaginationLink></PaginationItem><PaginationItem><PaginationLink href="#">2</PaginationLink></PaginationItem><PaginationItem><PaginationNext href="#" /></PaginationItem></PaginationContent></Pagination>}
        </>}
      </div>
    </div>
  )
}
