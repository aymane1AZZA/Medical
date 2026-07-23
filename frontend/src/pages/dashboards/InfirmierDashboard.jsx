import { FiActivity, FiClipboard, FiHeart, FiThermometer } from 'react-icons/fi';
import Loader from '../../components/Loader.jsx';
import PermissionGrid from '../../components/PermissionGrid.jsx';
import StatCard from '../../components/StatCard.jsx';
import DashboardLayout from '../../layouts/DashboardLayout.jsx';
import { useDashboard } from '../../hooks/useDashboard';

export default function InfirmierDashboard() {
  const { dashboard, loading, error } = useDashboard('ROLE_INFERMIER');

  return (
    <DashboardLayout title="Poste de soins" eyebrow="Suivi infirmier">
      {loading && <Loader label="Chargement du tableau de bord" />}
      {error && <p className="rounded-lg border border-red-200 bg-red-50 p-4 text-sm font-semibold text-chu-red">{error}</p>}
      {dashboard && (
        <div className="space-y-6">
          <div className="grid gap-4 md:grid-cols-4">
            <StatCard label="Patients du service" value="24" icon={FiHeart} />
            <StatCard label="Constantes" value="68" icon={FiThermometer} tone="text-chu-red" />
            <StatCard label="Soins planifiés" value="31" icon={FiClipboard} tone="text-chu-sky" />
            <StatCard label="Observations" value="15" icon={FiActivity} tone="text-indigo-600" />
          </div>
          <PermissionGrid items={dashboard.permissions} />
        </div>
      )}
    </DashboardLayout>
  );
}
