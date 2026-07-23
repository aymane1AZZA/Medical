import { ArcElement, Chart as ChartJS, Legend, Tooltip } from 'chart.js';
import { Doughnut } from 'react-chartjs-2';
import { Activity, Database, Server, Users } from 'lucide-react';
import Loader from '../../components/Loader.jsx';
import PermissionGrid from '../../components/PermissionGrid.jsx';
import StatCard from '../../components/StatCard.jsx';
import DashboardLayout from '../../layouts/DashboardLayout.jsx';
import { useDashboard } from '../../hooks/useDashboard';

ChartJS.register(ArcElement, Tooltip, Legend);

export default function AdminDashboard() {
  const { dashboard, loading, error } = useDashboard('ROLE_ADMIN');
  const chartData = {
    labels: ['Médecins', 'Infirmiers', 'Patients', 'Laboratoire'],
    datasets: [
      {
        data: [34, 82, 420, 18],
        backgroundColor: ['#0369a1', '#047857', '#0f766e', '#7c3aed'],
        borderWidth: 0,
      },
    ],
  };

  return (
    <DashboardLayout title="Administration centrale" eyebrow="Gouvernance SIH">
      {loading && <Loader label="Chargement du tableau de bord" />}
      {error && <p className="rounded-lg border border-red-200 bg-red-50 p-4 text-sm font-semibold text-chu-red">{error}</p>}
      {dashboard && (
        <div className="space-y-6">
          <div className="grid gap-4 md:grid-cols-4">
            <StatCard label="Utilisateurs" value="554" icon={Users} />
            <StatCard label="Services" value="18" icon={Database} tone="text-info" />
            <StatCard label="Équipements" value="246" icon={Server} tone="text-primary" />
            <StatCard label="Alertes" value="7" icon={Activity} tone="text-destructive" />
          </div>
          <div className="grid gap-6 lg:grid-cols-[0.72fr_0.28fr]">
            <PermissionGrid items={dashboard.permissions} />
            <section className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
              <h3 className="text-lg font-bold text-chu-ink">Répartition</h3>
              <div className="mx-auto mt-4 max-w-64">
                <Doughnut data={chartData} options={{ plugins: { legend: { position: 'bottom' } }, cutout: '62%' }} />
              </div>
            </section>
          </div>
        </div>
      )}
    </DashboardLayout>
  );
}
