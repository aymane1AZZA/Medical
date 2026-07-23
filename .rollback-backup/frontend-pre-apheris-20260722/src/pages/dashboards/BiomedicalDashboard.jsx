import { FiAlertTriangle, FiCpu, FiSettings, FiTool } from 'react-icons/fi';
import Loader from '../../components/Loader.jsx';
import PermissionGrid from '../../components/PermissionGrid.jsx';
import StatCard from '../../components/StatCard.jsx';
import DashboardLayout from '../../layouts/DashboardLayout.jsx';
import { useDashboard } from '../../hooks/useDashboard';

export default function BiomedicalDashboard() {
  const { dashboard, loading, error } = useDashboard('ROLE_BIOMEDICAL');

  return (
    <DashboardLayout title="Maintenance biomédicale" eyebrow="Plateau technique">
      {loading && <Loader label="Chargement du tableau de bord" />}
      {error && <p className="rounded-lg border border-red-200 bg-red-50 p-4 text-sm font-semibold text-chu-red">{error}</p>}
      {dashboard && (
        <div className="space-y-6">
          <div className="grid gap-4 md:grid-cols-4">
            <StatCard label="Équipements actifs" value="246" icon={FiCpu} />
            <StatCard label="Maintenances" value="19" icon={FiTool} tone="text-chu-sky" />
            <StatCard label="Pannes ouvertes" value="6" icon={FiAlertTriangle} tone="text-chu-red" />
            <StatCard label="Interventions" value="43" icon={FiSettings} tone="text-indigo-600" />
          </div>
          <PermissionGrid items={dashboard.permissions} />
        </div>
      )}
    </DashboardLayout>
  );
}
