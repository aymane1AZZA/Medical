import { FiCalendar, FiDownload, FiFileText, FiHeart } from 'react-icons/fi';
import Loader from '../../components/Loader.jsx';
import PermissionGrid from '../../components/PermissionGrid.jsx';
import StatCard from '../../components/StatCard.jsx';
import DashboardLayout from '../../layouts/DashboardLayout.jsx';
import { useDashboard } from '../../hooks/useDashboard';

export default function PatientDashboard() {
  const { dashboard, loading, error } = useDashboard('ROLE_PATIENT');

  return (
    <DashboardLayout title="Mon espace santé" eyebrow="Dossier patient">
      {loading && <Loader label="Chargement du tableau de bord" />}
      {error && <p className="rounded-lg border border-red-200 bg-red-50 p-4 text-sm font-semibold text-chu-red">{error}</p>}
      {dashboard && (
        <div className="space-y-6">
          <div className="grid gap-4 md:grid-cols-4">
            <StatCard label="Documents" value="14" icon={FiFileText} />
            <StatCard label="Rendez-vous" value="3" icon={FiCalendar} tone="text-chu-sky" />
            <StatCard label="Prescriptions" value="5" icon={FiHeart} tone="text-chu-emerald" />
            <StatCard label="Téléchargements" value="8" icon={FiDownload} tone="text-indigo-600" />
          </div>
          <PermissionGrid items={dashboard.permissions} />
        </div>
      )}
    </DashboardLayout>
  );
}
