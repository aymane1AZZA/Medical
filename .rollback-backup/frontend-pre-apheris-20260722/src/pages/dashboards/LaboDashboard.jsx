import { BarElement, CategoryScale, Chart as ChartJS, LinearScale, Tooltip } from 'chart.js';
import { Bar } from 'react-chartjs-2';
import { FiCheckSquare, FiDroplet, FiFilePlus, FiSend } from 'react-icons/fi';
import Loader from '../../components/Loader.jsx';
import PermissionGrid from '../../components/PermissionGrid.jsx';
import StatCard from '../../components/StatCard.jsx';
import DashboardLayout from '../../layouts/DashboardLayout.jsx';
import { useDashboard } from '../../hooks/useDashboard';

ChartJS.register(CategoryScale, LinearScale, BarElement, Tooltip);

export default function LaboDashboard() {
  const { dashboard, loading, error } = useDashboard('ROLE_LABO');
  const chartData = {
    labels: ['Biochimie', 'Hémato', 'Microbio', 'Immuno'],
    datasets: [
      {
        data: [18, 25, 11, 7],
        backgroundColor: ['#0f766e', '#0369a1', '#7c3aed', '#047857'],
        borderRadius: 6,
      },
    ],
  };

  return (
    <DashboardLayout title="Laboratoire central" eyebrow="Analyses médicales">
      {loading && <Loader label="Chargement du tableau de bord" />}
      {error && <p className="rounded-lg border border-red-200 bg-red-50 p-4 text-sm font-semibold text-chu-red">{error}</p>}
      {dashboard && (
        <div className="space-y-6">
          <div className="grid gap-4 md:grid-cols-4">
            <StatCard label="Demandes" value="61" icon={FiFilePlus} />
            <StatCard label="Prélèvements" value="47" icon={FiDroplet} tone="text-chu-sky" />
            <StatCard label="Validées" value="36" icon={FiCheckSquare} tone="text-chu-emerald" />
            <StatCard label="Transmises" value="32" icon={FiSend} tone="text-indigo-600" />
          </div>
          <div className="grid gap-6 lg:grid-cols-[0.65fr_0.35fr]">
            <PermissionGrid items={dashboard.permissions} />
            <section className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
              <h3 className="text-lg font-bold text-chu-ink">Flux du jour</h3>
              <div className="mt-4">
                <Bar data={chartData} options={{ plugins: { legend: { display: false } }, scales: { y: { beginAtZero: true } } }} />
              </div>
            </section>
          </div>
        </div>
      )}
    </DashboardLayout>
  );
}
