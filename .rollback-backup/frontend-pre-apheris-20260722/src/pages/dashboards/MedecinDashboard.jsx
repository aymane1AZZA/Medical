import { FiCalendar, FiClipboard, FiFileText, FiUserCheck } from 'react-icons/fi';
import Loader from '../../components/Loader.jsx';
import PermissionGrid from '../../components/PermissionGrid.jsx';
import StatCard from '../../components/StatCard.jsx';
import DashboardLayout from '../../layouts/DashboardLayout.jsx';
import { useDashboard } from '../../hooks/useDashboard';

export default function MedecinDashboard() {
  const { dashboard, loading, error } = useDashboard('ROLE_MEDECIN');

  return (
    <DashboardLayout title="Cabinet médical" eyebrow="Parcours de soins">
      {loading && <Loader label="Chargement du tableau de bord" />}
      {error && <p className="rounded-lg border border-red-200 bg-red-50 p-4 text-sm font-semibold text-chu-red">{error}</p>}
      {dashboard && (
        <div className="space-y-6">
          <div className="grid gap-4 md:grid-cols-4">
            <StatCard label="Patients suivis" value="38" icon={FiUserCheck} />
            <StatCard label="Consultations" value="12" icon={FiClipboard} tone="text-chu-sky" />
            <StatCard label="Prescriptions" value="21" icon={FiFileText} tone="text-indigo-600" />
            <StatCard label="Rendez-vous" value="9" icon={FiCalendar} tone="text-chu-emerald" />
          </div>
          <PermissionGrid items={dashboard.permissions} />
        </div>
      )}
    </DashboardLayout>
  );
}
