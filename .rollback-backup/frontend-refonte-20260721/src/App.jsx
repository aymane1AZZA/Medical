import { lazy, Suspense } from 'react'
import { Navigate, Route, Routes } from 'react-router-dom'
import ProtectedRoute from '@/routes/ProtectedRoute'
import LoginPage from '@/pages/LoginPage'
import ForgotPasswordPage from '@/pages/ForgotPasswordPage'
import { LoadingState } from '@/components/clinical/ClinicalStates'

const AppShell = lazy(() => import('@/layouts/AppShell'))
const PatientsListPage = lazy(() => import('@/pages/patients/PatientsListPage'))
const PatientRecordPage = lazy(() => import('@/pages/patients/PatientRecordPage'))
const PlanningPage = lazy(() => import('@/pages/patients/PlanningPage'))
const SessionCockpitPage = lazy(() => import('@/pages/sessions/SessionCockpitPage'))
const AdminDashboard = lazy(() => import('@/pages/dashboards/AdminDashboard'))
const BiomedicalDashboard = lazy(() => import('@/pages/dashboards/BiomedicalDashboard'))
const PatientDashboard = lazy(() => import('@/pages/dashboards/PatientDashboard'))
const LaboDashboard = lazy(() => import('@/pages/dashboards/LaboDashboard'))

function RouteLoader() {
  return <div className="p-4 sm:p-6"><LoadingState rows={7} label="Chargement de l’espace clinique" /></div>
}

export default function App() {
  return (
    <Suspense fallback={<RouteLoader />}>
      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/mot-de-passe-oublie" element={<ForgotPasswordPage />} />

        <Route element={<ProtectedRoute allowedRoles={['ROLE_MEDECIN', 'ROLE_INFERMIER', 'ROLE_ADMIN']} />}>
          <Route element={<AppShell />}>
            <Route path="/medecin" element={<Navigate to="/medecin/patients" replace />} />
            <Route path="/medecin/patients" element={<PatientsListPage />} />
            <Route path="/medecin/patients/:patientId" element={<PatientRecordPage />} />
            <Route path="/medecin/patients/:patientId/planification" element={<PlanningPage />} />
            <Route path="/infirmier" element={<Navigate to="/infirmier/seances/SEA-2026-0720-04" replace />} />
            <Route path="/infirmier/seances/:sessionId" element={<SessionCockpitPage />} />
          </Route>
        </Route>

        <Route element={<ProtectedRoute allowedRoles={['ROLE_ADMIN']} />}><Route path="/admin" element={<AdminDashboard />} /></Route>
        <Route element={<ProtectedRoute allowedRoles={['ROLE_BIOMEDICAL']} />}><Route path="/biomedical" element={<BiomedicalDashboard />} /></Route>
        <Route element={<ProtectedRoute allowedRoles={['ROLE_PATIENT']} />}><Route path="/patient" element={<PatientDashboard />} /></Route>
        <Route element={<ProtectedRoute allowedRoles={['ROLE_LABO']} />}><Route path="/labo" element={<LaboDashboard />} /></Route>
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </Suspense>
  )
}
