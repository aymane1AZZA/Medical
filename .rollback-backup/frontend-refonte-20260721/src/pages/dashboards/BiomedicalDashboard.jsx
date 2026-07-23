import { Cpu, Settings, TriangleAlert, Wrench } from 'lucide-react';
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
            <StatCard label="Équipements actifs" value="246" icon={Cpu} />
            <StatCard label="Maintenances" value="19" icon={Wrench} tone="text-info" />
            <StatCard label="Pannes ouvertes" value="6" icon={TriangleAlert} tone="text-destructive" />
            <StatCard label="Interventions" value="43" icon={Settings} tone="text-primary" />
          </div>
          <PermissionGrid items={dashboard.permissions} />
        </div>
      )}
    </DashboardLayout>
  );
}
