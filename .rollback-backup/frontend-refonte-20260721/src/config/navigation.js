import {
  Activity,
  CalendarDays,
  ClipboardCheck,
  FileHeart,
  FlaskConical,
  Gauge,
  LayoutDashboard,
  Settings,
  ShieldCheck,
  Stethoscope,
  Users,
  Wrench,
} from 'lucide-react'

const sharedClinical = [
  { label: 'Patients', to: '/medecin/patients', icon: Users },
  { label: 'Dossier de référence', to: '/medecin/patients/pat-001', icon: FileHeart },
  { label: 'Planification', to: '/medecin/patients/pat-001/planification', icon: CalendarDays },
  { label: 'Cockpit de séance', to: '/infirmier/seances/SEA-2026-0720-04', icon: Activity },
]

export const navigationByRole = {
  ROLE_MEDECIN: [
    { title: 'Parcours patient', items: sharedClinical },
    { title: 'Décision médicale', items: [
      { label: 'Prescriptions', to: '/medecin/patients/pat-001?tab=prescription', icon: ClipboardCheck },
      { label: 'Bilans biologiques', to: '/medecin/patients/pat-001?tab=biology', icon: FlaskConical },
    ] },
  ],
  ROLE_INFERMIER: [
    { title: 'Séances', items: [
      { label: 'Vue du jour', to: '/infirmier', icon: LayoutDashboard },
      { label: 'Cockpit en cours', to: '/infirmier/seances/SEA-2026-0720-04', icon: Gauge },
      { label: 'Préparation', to: '/medecin/patients/pat-001?tab=planning', icon: ClipboardCheck },
    ] },
  ],
  ROLE_LABO: [{ title: 'Laboratoire', items: [{ label: 'Analyses', to: '/labo', icon: FlaskConical }] }],
  ROLE_BIOMEDICAL: [{ title: 'Plateau technique', items: [{ label: 'Équipements', to: '/biomedical', icon: Wrench }] }],
  ROLE_ADMIN: [{ title: 'Administration', items: [
    { label: 'Pilotage', to: '/admin', icon: ShieldCheck },
    { label: 'Patients', to: '/medecin/patients', icon: Users },
    { label: 'Référentiels', to: '/admin', icon: Settings },
  ] }],
  ROLE_PATIENT: [{ title: 'Mon parcours', items: [{ label: 'Mon espace', to: '/patient', icon: Stethoscope }] }],
}

export const roleProfession = {
  ROLE_ADMIN: 'Administrateur',
  ROLE_MEDECIN: 'Médecin spécialiste',
  ROLE_INFERMIER: 'Infirmier',
  ROLE_LABO: 'Personnel de laboratoire',
  ROLE_BIOMEDICAL: 'Ingénieur biomédical',
  ROLE_PATIENT: 'Patient',
}
